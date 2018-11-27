import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FuncoesBD {
    private Connection connection;
    private StringMethods sm = new StringMethods();
    public FuncoesBD(Connection connection) {
        this.connection = connection;
    }

    //REGISTO
    public String registaUser(HashMap<String, String> map) throws SQLException {
        String resposta = "*\n*";
        User user = new User(map.get("username"), map.get("password"), "offline", "contributor"); //status-> null (ato de registar)

        //teste
        PreparedStatement st = connection.prepareStatement(sm.verificaUser);
        st.setString(1, user.username);
        st.setString(2, user.password);
        st.setString(3, user.status);
        st.setString(4, user.what); //-> ver se cria default

        try (ResultSet rs = st.executeQuery()) {

        } catch (SQLException e) {
            // Other SQL Exception
            if (e.getSQLState().equals("23505")) {
                // Duplicate entry
                System.out.println("Já existe este username.");
                resposta = "type|register;username|" + user.username + ";password|" + user.password + ";id|" + map.get("id") + ";status|rejected"; //o username ja existe
            } else {
                //System.out.println("Great success!");
                st.close();
                Statement stTemp = connection.createStatement();
                ResultSet rsTemp = stTemp.executeQuery("SELECT COUNT(*) AS rowcount FROM utilizador");
                rsTemp.next();
                if(rsTemp.getInt("rowcount") == 1) {
                    stTemp.close();
                    rsTemp.close();
                    PreparedStatement stTempAux = connection.prepareStatement("UPDATE utilizador set what = ? where username = ?");
                    stTempAux.setString(1, "admin");
                    stTempAux.setString(2, user.username);
                    System.out.println("Updated " + stTempAux.executeUpdate() + " row(s)");
                } else {
                    System.out.println("Numero de linhas: "+rsTemp.getInt("rowcount"));
                }
                resposta = "type|register;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted"; //successful
            }
        }
        return resposta;
    }

    //LOGIN
    public String loginUser(HashMap<String, String> map) throws SQLException {
        String tempResposta = "dunno";
        User user = new User(map.get("username"), map.get("password"), "accepted", "normal"); //status -> accepted
        int temp = checkPassword(user);
        if(temp == 2){ //existe essa conta e a pass esta certa
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
            //turnOnline(aux, user);
        } else if(temp == 1) { //username ja usado, pass errada
            System.out.println("Username ja utilizado");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongpassword";
        } else if(temp == 0){
            System.out.println("Username nao existe");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongusername";
        }
        return tempResposta;
    }

    public int checkPassword(User user) throws SQLException {
        PreparedStatement st = connection.prepareStatement("SELECT password from utilizador where username = ?");
        st.setString(1, user.username);
        try (ResultSet rs = st.executeQuery()) {
            rs.next();
            if(rs.getString("password").equals(user.password)) {
                return 2;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    //DAR PERMISSOES DE EDITOR
    public String tornarEditor(HashMap<String, String> map) throws SQLException {
        String tempResposta;
        User user = new User(map.get("username"), map.get("password"), "accepted", map.get("what")); //status -> accepted
        User userDest = new User(map.get("usernameDest"), "null", "null", map.get("null")); //user que quero promover

        //verifica se existe User
        PreparedStatement st = connection.prepareStatement("select username from utilizador where username = ?");
        st.setString(1, userDest.username);
        try (ResultSet rs = st.executeQuery()) {
            if(rs.next() == false) {
                System.out.println("nao existe o user a ser promovido");
                tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusernameDest;";
                return tempResposta;
            }
            System.out.println("existe o user que quer promover");
        } catch (SQLException e) { //caso nao exista o user a ser promovido
            System.out.println("nao existe o user a ser promovido");
            tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusernameDest;";
            return tempResposta;
        }
        st.close();
        //existe o user a promover
        //verificar se o user a tentar promover tem permissoes
        if(checkPermission(user)) {
            PreparedStatement stf = connection.prepareStatement("update utilizador set what = ? where username = ?");
            stf.setString(1, "editor");
            stf.setString(2, userDest.username);
            stf.executeUpdate();
            tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|accepted;";
            return tempResposta;
        } else {
            //caso o user nao tenha permissao
            tempResposta = "type|promote;username|" + user.username + ";password|" + user.password + ";usernameDest|" + map.get("usernameDest") + ";id|" + map.get("id") + ";status|rejected;";
            return tempResposta;
        }
    }

    //ADD ARTISTA
    public String addArtista(HashMap<String, String> map) throws SQLException {
        String resposta = "\nnnn\n";
        ArrayList<Album> albums = new ArrayList<>();
        Artista artista = new Artista(map.get("artist_name"), map.get("description"), map.get("birth_date"), albums);
        String grupo = map.get("grupo");
        User user = new User(map.get("username"), "null", "null", "null");
        //verificar se tem permissoes para adicionar
        if(!checkPermission(user)){
            return "type|new_artist;username|"+map.get("username")+"artist_name|"+artista.nome+";id|"+map.get("id")+";status|nopermission";
        }
        if(!checkArtist(artista)) {
            PreparedStatement st = connection.prepareStatement("insert into artista values (?,?,default,?)");
            st.setString(1, artista.nome);
            st.setString(2, artista.descricao);
            st.setObject(3, string2Date(artista.dataNascimento));
            st.executeUpdate();
            if(grupo.equals(" ")) {//caso o artista seja musico sem grupo
                addMusico(getIdArtista(artista));
            } else if(grupo.equals(artista.nome)) { //caso o artista a adicionar é um grupo
                addGrupo(artista);
            } else { //caso o artista a adicionar faca parte de um grupo
                addMusico2Grupo(artista, grupo);
            }
            System.out.println("\nInseri artista");
            resposta = "type|new_artist;username|"+map.get("username")+";artist_name|"+artista.nome+";id|"+map.get("id")+";status|accepted";
            return resposta;
        } else {
            // Duplicate entry
            System.out.println("\nja existe o artista!!");
            resposta = "type|new_artist;username|" + map.get("username") + "artist_name|" + artista.nome + ";id|" + map.get("id") + ";status|rejected";
            return resposta;
        }
    }

    public boolean checkPermission(User user) throws SQLException {
        PreparedStatement st1 = connection.prepareStatement("select what from utilizador where username = ?");
        st1.setString(1, user.username);
        ResultSet rs1 = st1.executeQuery();
        rs1.next();
        if(rs1.getString("what").equals("editor")||rs1.getString("what").equals("admin")) {
            //caso tenha permissoes e o user exista
            st1.close();
            rs1.close();
            return true;
        } else {
            //caso o user nao tenha permissao
            rs1.close();
            st1.close();
            return false;
        }
    }

    public boolean checkArtist(Artista artista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select nome from artista where nome = ?");
        st.setString(1, artista.nome);
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            return true;
        } else {
            return false;
        }
    }
    
    public LocalDate string2Date(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateString,formatter);
        return date;
    }

    //ADD ALBUM
    public String addAlbum(HashMap<String, String> map) throws SQLException {
        ArrayList<Album> albums = new ArrayList<>();
        Artista artista = new Artista(map.get("artist_name"), map.get("null"), map.get("null"), albums);
        //verificar se tem permissoes para adicionar
        User user = new User(map.get("username"), "null", "null", "null");
        if(!checkPermission(user)){
            return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|nopermission";
        }
        ArrayList<Review> reviews = new ArrayList<>();
        ArrayList<Musica> musicas = new ArrayList<>();
        Album album = new Album(map.get("artist_name"), map.get("description"), map.get("album_name"), (float)0, reviews, musicas, map.get("genre"), map.get("releaseDate"), map.get("recordlabel"));

        if (checkArtist(artista)) { //caso exista o artistA
            System.out.println("artista existe");
            if(checkAlbumArtista(album, artista)){ //caso o album ja exista no artista
                System.out.println("\nja existe album\n");
                return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|rejected";
            } else {
                PreparedStatement st = connection.prepareStatement("insert into album values(?,?,?,?,?,default,?) returning idalbum");
                st.setString(1, album.nome);
                st.setString(2, album.descricao);
                st.setFloat(3, (float)0);
                st.setString(4, album.genero);
                st.setObject(5, string2Date(album.dataLancamento));
                st.setInt(6, getIdArtista(artista));
                ResultSet rs = st.executeQuery();
                rs.next();
                int idAlbum = rs.getInt(1);
                //verificar genero
                if(checkGenero(album.genero)) { //caso ja exista
                    System.out.println("genero de album ja existe, adicionei album");
                    PreparedStatement st1 = connection.prepareStatement("insert into album_genero values(?,?)");
                    st1.setInt(1, idAlbum);
                    st1.setString(2, album.genero);
                    st1.executeUpdate();
                } else { //caso ainda nao exista -> VERIFICAR SE CRIA NA TABELA ALBUM_GENERO
                    System.out.println("nao existe genero, adicionei genero e album");
                    PreparedStatement st1 = connection.prepareStatement("insert into genero values(?)");
                    st1.setString(1, album.genero);
                    st1.executeUpdate();
                    PreparedStatement st3 = connection.prepareStatement("insert into album_genero values(?,?)");
                    st3.setInt(1, idAlbum);
                    st3.setString(2, album.genero);
                    st3.executeUpdate();
                }
                //verificar recordLabel
                PreparedStatement st2 = connection.prepareStatement("insert into editora values(?,?)");
                st2.setString(1, album.genero);
                st2.setInt(2, idAlbum);
                st2.executeUpdate();
                return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|accepted";
            }
        } else {
            System.out.println("artista nao existe");
            return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|noartist";
        }
    }

    public HashMap<String, String> artista2Hashmap(Artista artista) {
        HashMap<String, String> res = new HashMap<>();
        res.put("artist_name", artista.nome);
        res.put("description", artista.descricao);
        res.put("birth_date", artista.dataNascimento);
        return res;
    }

    public boolean checkAlbumArtista(Album album, Artista artista) throws SQLException {
        int idTemp = getIdArtista(artista);
        PreparedStatement st = connection.prepareStatement("select album.nome from album, artista where album.nome = ? and album.artista_idartista = ?");
        st.setString(1, album.nome);
        st.setInt(2, idTemp);
        ResultSet rs  = st.executeQuery();
        if(rs.next()) { //caso ja exista um album com o mesmo nome no artista;
            System.out.println("\nesta aqui p album:\n"+rs.getString(1));
            return true;
        } else {
            return false;
        }
    }

    //retorna o id do artista (obrigatorio existir artista)
    public int getIdArtista(Artista artista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select idartista from artista where nome = ?");
        st.setString(1, artista.nome);
        ResultSet rs = st.executeQuery();
        rs.next();
        return rs.getInt("idartista");
    }

//    public int getIdAlbum(Album album) {
//        PreparedStatement st = connection.prepareStatement("select idalbum from album where ")
//    }

    public boolean checkEditora(String editora) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select * from editora where nome = ?");
        st.setString(1, editora);
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean checkGenero(String genero) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select nome from genero where nome = ?");
        st.setString(1, genero);
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public void addMusico(int idArtista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("insert into musico values(?)");
        st.setInt(1, idArtista);
        st.executeUpdate();
    }
    public void addGrupo(Artista artista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("insert into grupo values(?,?,?)");
        st.setString(1, artista.nome);
        st.setObject(2, string2Date(artista.dataNascimento));
        st.setString(3, artista.descricao);
        st.executeUpdate();
    }
    public void addMusico2Grupo(Artista musico, String grupo) throws SQLException {
        PreparedStatement st = connection.prepareStatement("insert into grupo_artista values(?,?)");
        st.setString(1, grupo);
        st.setInt(2, getIdArtista(musico));
        st.executeUpdate();
    }
}
