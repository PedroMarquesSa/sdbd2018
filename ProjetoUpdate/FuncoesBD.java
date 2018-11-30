import java.sql.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
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
                if (rsTemp.getInt("rowcount") == 1) {
                    stTemp.close();
                    rsTemp.close();
                    PreparedStatement stTempAux = connection.prepareStatement("UPDATE utilizador set what = ? where username = ?");
                    stTempAux.setString(1, "admin");
                    stTempAux.setString(2, user.username);
                    System.out.println("Updated " + stTempAux.executeUpdate() + " row(s)");
                } else {
                    System.out.println("Numero de linhas: " + rsTemp.getInt("rowcount"));
                }
                resposta = "type|register;username|" + user.username + ";password|" + user.password + ";id|" + map.get("id") + ";status|accepted"; //successful
            }
        }
        return resposta;
    }

    //LOGIN
    public String loginUser(HashMap<String, String> map) throws SQLException {
        String tempResposta = "dunno";
        User user = new User(map.get("username"), map.get("password"), "accepted", "normal"); //status -> accepted
        int temp = checkPassword(user);
        if (temp == 3) { //existe essa conta e a pass esta certa
            tempResposta = "type|login;username|" + user.username + ";password|" + user.password + ";what|editor;id|" + map.get("id") + ";status|accepted";
            //turnOnline(aux, user);
        }
        if (temp == 2) { //existe essa conta e a pass esta certa
            tempResposta = "type|login;username|" + user.username + ";password|" + user.password + ";what|normal;id|" + map.get("id") + ";status|accepted";
            //turnOnline(aux, user);
        } else if (temp == 1) { //username ja usado, pass errada
            System.out.println("Username ja utilizado");
            tempResposta = "type|login;username|" + user.username + ";password|" + user.password + ";id|" + map.get("id") + ";status|wrongpassword";
        } else if (temp == 0) {
            System.out.println("Username nao existe");
            tempResposta = "type|login;username|" + user.username + ";password|" + user.password + ";id|" + map.get("id") + ";status|wrongusername";
        }
        return tempResposta;
    }

    public int checkPassword(User user) throws SQLException {
        PreparedStatement st = connection.prepareStatement("SELECT password, what from utilizador where username = ?");
        st.setString(1, user.username);
        try (ResultSet rs = st.executeQuery()) {
            rs.next();
            if (rs.getString("password").equals(user.password)) {
                if (rs.getString("what").equals("editor") || rs.getString("what").equals("admin")) {
                    return 3;
                }
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
            if (rs.next() == false) {
                System.out.println("nao existe o user a ser promovido");
                tempResposta = "type|promote;username|" + user.username + ";password|" + user.password + ";usernameDest|" + map.get("usernameDest") + ";id|" + map.get("id") + ";status|wrongusernameDest;";
                return tempResposta;
            }
            System.out.println("existe o user que quer promover");
        } catch (SQLException e) { //caso nao exista o user a ser promovido
            System.out.println("nao existe o user a ser promovido");
            tempResposta = "type|promote;username|" + user.username + ";password|" + user.password + ";usernameDest|" + map.get("usernameDest") + ";id|" + map.get("id") + ";status|wrongusernameDest;";
            return tempResposta;
        }
        st.close();
        //existe o user a promover
        //verificar se o user a tentar promover tem permissoes
        if (checkPermission(user)) {
            PreparedStatement stf = connection.prepareStatement("update utilizador set what = ? where username = ?");
            stf.setString(1, "editor");
            stf.setString(2, userDest.username);
            stf.executeUpdate();
            tempResposta = "type|promote;username|" + user.username + ";password|" + user.password + ";usernameDest|" + map.get("usernameDest") + ";id|" + map.get("id") + ";status|accepted;";
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
        Artista artista = new Artista(map.get("artist_name"), map.get("description"), map.get("birth_date"));
        String grupo = map.get("grupo");
        User user = new User(map.get("username"), "null", "null", "null");
        //verificar se tem permissoes para adicionar
        if (!checkPermission(user)) {
            return "type|new_artist;username|" + map.get("username") + ";artist_name|" + artista.nome + ";id|" + map.get("id") + ";status|nopermission";
        }
        if (!checkArtist(artista)) {
            PreparedStatement st = connection.prepareStatement("insert into artista values (?,?,default,?)");
            st.setString(1, artista.nome);
            st.setString(2, artista.descricao);
            st.setObject(3, string2Date(artista.dataNascimento));
            st.executeUpdate();
            if (grupo.equals(" ")) {//caso o artista seja musico sem grupo
                addMusico(getIdArtista(artista));
            } else if (grupo.equals(artista.nome)) { //caso o artista a adicionar é um grupo
                addGrupo(artista);
            } else { //caso o artista a adicionar faca parte de um grupo
                addMusico(getIdArtista(artista));
                addMusico2Grupo(artista, grupo);
            }
            System.out.println("\nInseri artista");
            resposta = "type|new_artist;username|" + map.get("username") + ";artist_name|" + artista.nome + ";id|" + map.get("id") + ";status|accepted";
            return resposta;
        } else {
            // Duplicate entry
            System.out.println("\nja existe o artista!!");
            resposta = "type|new_artist;username|" + map.get("username") + ";artist_name|" + artista.nome + ";id|" + map.get("id") + ";status|rejected";
            return resposta;
        }
    }

    public boolean checkPermission(User user) throws SQLException {
        PreparedStatement st1 = connection.prepareStatement("select what from utilizador where username = ?");
        st1.setString(1, user.username);
        ResultSet rs1 = st1.executeQuery();
        rs1.next();
        if (rs1.getString("what").equals("editor") || rs1.getString("what").equals("admin")) {
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
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public LocalDate string2Date(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    //ADD ALBUM
    public String addAlbum(HashMap<String, String> map) throws SQLException {
        ArrayList<Album> albums = new ArrayList<>();
        Artista artista = new Artista(map.get("artist_name"), map.get("null"), map.get("null"));
        //verificar se tem permissoes para adicionar
        User user = new User(map.get("username"), "null", "null", "null");
        if (!checkPermission(user)) {
            return "type|new_album;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";id|" + map.get("id") + ";status|nopermission";
        }
        Album album = new Album(map.get("artist_name"), map.get("description"), map.get("album_name"), (float) 0, map.get("genre"), map.get("releaseDate"), map.get("recordlabel"));

        if (checkArtist(artista)) { //caso exista o artistA
            System.out.println("artista existe");
            if (checkAlbumArtista(album, artista)) { //caso o album ja exista no artista
                System.out.println("\nja existe album\n");
                return "type|new_album;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + album.nome + ";id|" + map.get("id") + ";status|rejected";
            } else {
                PreparedStatement st = connection.prepareStatement("insert into album values(?,?,?,?,?,default,?) returning idalbum");
                st.setString(1, album.nome);
                st.setString(2, album.descricao);
                st.setFloat(3, (float) 0);
                st.setString(4, album.genero);
                st.setObject(5, string2Date(album.dataLancamento));
                st.setInt(6, getIdArtista(artista));
                ResultSet rs = st.executeQuery();
                rs.next();
                int idAlbum = rs.getInt(1);
                //verificar genero
                if (checkGenero(album.genero)) { //caso ja exista
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
                st2.setString(1, album.editora);
                st2.setInt(2, idAlbum);
                st2.executeUpdate();
                return "type|new_album;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + album.nome + ";id|" + map.get("id") + ";status|accepted";
            }
        } else {
            System.out.println("artista nao existe");
            return "type|new_album;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + album.nome + ";id|" + map.get("id") + ";status|noartist";
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
        PreparedStatement st = connection.prepareStatement("select idalbum from album, artista where album.nome = ? and album.artista_idartista = (select idartista from artista where nome = ?)");
        st.setString(1, album.nome);
        //System.out.println("\no album é: "+album.nome+"\n");
        st.setString(2, artista.nome);
        ResultSet rs = st.executeQuery();
        if (rs.next()) { //caso ja exista um album com o mesmo nome no artista;
            System.out.println("\nesta aqui o album:\n" + rs.getString(1));
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


    public boolean checkEditora(String editora) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select * from editora where nome = ?");
        st.setString(1, editora);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkGenero(String genero) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select nome from genero where nome = ?");
        st.setString(1, genero);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
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
    }

    public String addMusica(HashMap<String, String> map) throws SQLException {
        ArrayList<Album> albums = new ArrayList<>();
        Artista artista = new Artista(map.get("artist_name"), map.get("null"), map.get("null"));
        //verificar se tem permissoes para adicionar
        User user = new User(map.get("username"), "null", "null", "null");
        if (!checkPermission(user)) {
            return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|nopermission";
        }
        ArrayList<Review> reviews = new ArrayList<>();
        ArrayList<Musica> musicas = new ArrayList<>();
        Album album = new Album(artista.nome, "", map.get("album_name"), (float) 0, "", "", "");
        Musica musica = new Musica(map.get("music_name"), artista.nome, album.nome, map.get("lyrics"));
        //verificar se o artista existe
        if (!checkArtist(artista)) { //caso nao exista o artista
            System.out.println("\nnao existe o artista\n");
            return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|noartist";
        } //caso exista o artista
        if (!checkAlbumArtista(album, artista)) { //caso o album nao exista
            System.out.println("\nnao existe o album\n");
            return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|noalbum";
        } else {//caso o album exista
            if (checkMusicAlbumArtista(musica, album, artista)) { //caso ja exista a musica
                System.out.println("ja existe a musica");
                return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|rejected";
            } else {
                //adicionei na tabela musica
                System.out.println("consegui inserir!! props");
                PreparedStatement st = connection.prepareStatement("insert into musica values(?,default,?) returning idmusica");
                st.setString(1, musica.nome);
                st.setInt(2, getIdAlbum(album, artista));
                ResultSet rs = st.executeQuery();
                //adicionei na tabela letra
                rs.next();
                int idMusica = rs.getInt(1);
                PreparedStatement st1 = connection.prepareStatement("insert into letra values(?, ?)");
                st1.setString(1, musica.letra);
                st1.setInt(2, idMusica);
                st1.executeUpdate();
                return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|accepted";
            }
        }
    }

    public int getIdAlbum(Album album, Artista artista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select idalbum from album, artista where album.nome = ? and album.artista_idartista = (select idartista from artista where nome = ?)");
        st.setString(1, album.nome);
        st.setString(2, artista.nome);
        ResultSet rs = st.executeQuery();
        rs.next();
        return rs.getInt("idalbum");
    }

    public boolean checkMusicAlbumArtista(Musica musica, Album album, Artista artista) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select * from musica, album where musica.nome = ? and album_idalbum = (" +
                "select idalbum from album where album.nome = ? and album.artista_idartista = (select idartista from artista where nome = ?))");
        st.setString(1, musica.nome);
        st.setString(2, album.nome);
        st.setString(3, artista.nome);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public String writeReview(HashMap<String, String> map) throws SQLException {
        User user = new User(map.get("username"), "", "", ""); //user que quer escrever
        ArrayList<Album> albuns = new ArrayList<>();
        Album album = new Album(map.get("artist_name"), "", map.get("album_name"), 0, "", "", "");
        Artista artista = new Artista(map.get("artist_name"), "", "");
        if (checkPermission(user)) { //pode escever
            if (checkAlbumArtista(album, artista)) { //se existir
                int idAlbum = getIdAlbum(album, artista);
                PreparedStatement st = connection.prepareStatement("insert into review values(?, ?, ?, ?, ?)");
                st.setInt(1, Integer.parseInt(map.get("rating")));
                st.setString(2, map.get("critic"));
                st.setString(3, user.username);
                st.setInt(4, idAlbum);
                st.setString(5, user.username);
                st.executeUpdate();
                //ratingAVG
                int countReview;
                float sum = 0;
                PreparedStatement stCount = connection.prepareStatement("select count(*) from review where album_idalbum = ?");
                stCount.setInt(1, idAlbum);
                ResultSet rsCount = stCount.executeQuery();
                rsCount.next();
                countReview = rsCount.getInt(1);
                PreparedStatement stSum = connection.prepareStatement("select rating from review where album_idalbum = ?");
                stSum.setInt(1, idAlbum);
                ResultSet rsSum = stSum.executeQuery();
                while (rsSum.next()) {
                    sum += rsSum.getFloat(1);
                }
                if (countReview > 0) {
                    PreparedStatement stReview = connection.prepareStatement("update album set ratingavg = ? where idalbum = ?");
                    stReview.setFloat(1, sum / (float) countReview);
                    stReview.setInt(2, idAlbum);
                    stReview.executeUpdate();
                }
                return "type|album_critic;username|" + user.username + ";album_name|" + map.get("album_name") + ";critic|" + map.get("critic") + ";rating|" + map.get("rating") + ";id|" + map.get("id") + ";status|accepted";
            } //caso nao exista o album a rever
            return "type|album_critic;username|" + user.username + ";album_name|" + map.get("album_name") + ";critic|" + map.get("critic") + ";rating|" + map.get("rating") + ";id|" + map.get("id") + ";status|notfound";
        } else {// caso nao tenha permissao
            return "type|album_critic;username|" + user.username + ";album_name|" + map.get("album_name") + ";critic|" + map.get("critic") + ";rating|" + map.get("rating") + ";id|" + map.get("id") + ";status|nopermission";
        }
    }

    public String addConcert(HashMap<String, String> map) throws SQLException {
        User user = new User(map.get("username"), "", "", ""); //user que quer escrever
        String temp = map.get("artist_name");
        String[] artista = temp.split(",");
        System.out.println("artistas no concerto: " + map.get("artist_name"));
        if (!checkPermission(user)) {
            //nao pode escrever
            return "type|create_concert;username|" + user.username + ";artist_name|" + map.get("artist_name") + ";concert_name|" + map.get("concert_name") + ";date|" + map.get("date") + ";local|" + map.get("local") + ";id|" + map.get("id") + ";status|nopermission;";
        }
        //tem permissoes
        //ter todos os artistas para verificar
        ArrayList<Artista> artistas = new ArrayList<>();
        for (int i = 0; i < artista.length; i++) {
            artistas.add(new Artista(artista[i], "", ""));
            System.out.println("\nOs artistas para o concerto sao:" + artistas.get(i).nome + "\n");
        }
        int len = 0; //para ver se todos la estao
        for (Artista tempArtista : artistas) {
            if (checkArtist(tempArtista))
                len++;
        }
        if (len == artistas.size()) { //os artistas existem, pode adicionar
            PreparedStatement st = connection.prepareStatement("insert into concerto values(?, ?, default, ?) returning idconcerto");
            st.setObject(1, string2Date(map.get("date")));
            st.setString(2, map.get("local"));
            st.setString(3, map.get("concert_name"));
            try {
                ResultSet rs = st.executeQuery();
                rs.next();
                int idconcerto = rs.getInt(1);
                //inserir na outra tabela
                for (Artista auxArtista : artistas) {
                    PreparedStatement st1 = connection.prepareStatement("insert into artista_concerto values(?,?)");
                    st1.setInt(1, getIdArtista(auxArtista));
                    st1.setInt(2, idconcerto);
                    st1.executeUpdate();
                }
                System.out.println("deu para inserir concerto");
                return "type|create_concert;username|" + user.username + ";artist_name|" + map.get("artist_name") + ";concert_name|" + map.get("concert_name") + ";date|" + map.get("date") + ";local|" + map.get("local") + ";id|" + map.get("id") + ";status|accepted;";
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    System.out.println("ja existe um concerto ai");
                    return "type|create_concert;username|" + user.username + ";artist_name|" + map.get("artist_name") + ";concert_name|" + map.get("concert_name") + ";date|" + map.get("date") + ";local|" + map.get("local") + ";id|" + map.get("id") + ";status|reject;";
                }
            }
        } else { //nao existem todos os artistas
            return "type|create_concert;username|" + user.username + ";artist_name|" + map.get("artist_name") + ";concert_name|" + map.get("concert_name") + ";date|" + map.get("date") + ";local|" + map.get("local") + ";id|" + map.get("id") + ";status|noartists;";
        }
        System.out.println("nao sei o que se passou para aqui chegarmos #dunno");
        return "type|create_concert;username|" + user.username + ";artist_name|" + map.get("artist_name") + ";concert_name|" + map.get("concert_name") + ";date|" + map.get("date") + ";local|" + map.get("local") + ";id|" + map.get("id") + ";status|reject;";
    }

    public String searchAlbumTitle(HashMap<String, String> map) throws SQLException {
        Album album = new Album(map.get(""), "", map.get("album_name"), 0, "", "", "");
        PreparedStatement st = connection.prepareStatement("select nome from artista where idartista in (select artista_idartista from album where nome = ?)");
        st.setString(1, album.nome);
        ResultSet rs = st.executeQuery();
        ArrayList<String> artistas = new ArrayList<>();
        while (rs.next()) {
            System.out.println("entrei aqui");
            artistas.add(rs.getString(1));
        }
        if (artistas.isEmpty()) { //caso nao exista nenhum album com esse nome
            return "type|search_album_by_title;album_name|" + map.get("album_name") + ";id|" + map.get("id") + ";status|notfound";
        }
        String todos = "";
        for (String temp : artistas) {
            todos = todos.concat(temp + ",");
        }
        todos = todos.substring(0, todos.length() - 1);
        return "type|search_album_by_title;artist_name|" + todos + ";album_name|" + map.get("album_name") + ";id|" + map.get("id") + ";status|accepted;";
    }

    public String searchAlbumArtista(HashMap<String, String> map) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select nome from album where artista_idartista in (select idartista from artista where nome = ?)");
        st.setString(1, map.get("artist_name"));
        ResultSet rs = st.executeQuery();
        ArrayList<String> albums = new ArrayList<>();
        Artista artista = new Artista(map.get("artist_name"), "", "");
        if (checkArtist(artista)) { //caso o artista exista
            while (rs.next()) {
                albums.add(rs.getString(1));
            }
            if (albums.isEmpty()) { //caso nao exista nenhum album com esse nome
                return "type|search_album_by_artist;artist_name|" + map.get("artist_name") + ";id|" + map.get("id") + ";status|empty;";
            }
            String todos = "";
            for (String temp : albums) {
                todos = todos.concat(temp + "+");
            }
            if (todos.length() > 0)
                todos = todos.substring(0, todos.length() - 1);
            return "type|search_album_by_title;artist_name|" + map.get("artist_name") + ";count|" + Integer.toString(albums.size()) + ";album_name|" + todos + ";id|" + map.get("id") + ";status|accepted;";
        } else {
            //caso nao exista
            return "type|search_album_by_artist;artist_name|" + map.get("artist_name") + ";id|" + map.get("id") + ";status|notfound;";
        }
    }

    public String detalhesAlbum(HashMap<String, String> map) throws SQLException {
        PreparedStatement st = connection.prepareStatement("select descricao, ratingavg, genero, data from album where nome = ? and artista_idartista = (select idartista from artista where artista.nome = ?)");
        st.setString(1, map.get("album_name"));
        st.setString(2, map.get("artist_name"));
        ResultSet rs = st.executeQuery();
        int musicaCount = 0;
        int reviewCount = 0;
        if (rs.next()) { //caso encontre o album
            String descricao = rs.getString("descricao");
            String ratingAVG = Float.toString(rs.getFloat("ratingavg"));
            String genero = rs.getString("genero");
            String data = date2String(rs.getObject("data"));
            float rating = rs.getFloat("ratingAVG");
            StringBuilder musicas = new StringBuilder();
            String musica = "";
            StringBuilder reviews = new StringBuilder();
            //adicionar musicas
            PreparedStatement stMusicas = connection.prepareStatement("select nome from musica where album_idalbum = (select idalbum from album where album.nome = ? and album.artista_idartista = (select idartista from artista where nome = ?))");
            stMusicas.setString(1, map.get("album_name"));
            stMusicas.setString(2, map.get("artist_name"));
            ResultSet rsMusicas = stMusicas.executeQuery();
            while (rsMusicas.next()) {
                musica = rsMusicas.getString(1);
                musicas.append(musica).append("+");
                musicaCount++;
            }
            if (musicas.length() != 0)
                musicas.deleteCharAt(musicas.length() - 1);
            //adicionar reviews
            PreparedStatement stReviews = connection.prepareStatement("select rating, descricao from review where album_idalbum = (select idalbum from album where album.nome = ? and album.artista_idartista = (select idartista from artista where nome = ?))");
            stReviews.setString(1, map.get("album_name"));
            stReviews.setString(2, map.get("artist_name"));
            ResultSet rsReviews = stReviews.executeQuery();
            while (rsReviews.next()) {
                System.out.println("entrei aquieeee");
                reviews.append(rsReviews.getString("rating")).append("+").append(rsReviews.getString("descricao")).append("*");
                reviewCount++;
            }
            if (reviews.length() != 0)
                reviews.deleteCharAt(reviews.length() - 1);
            //criar a string a enviar
            return "type|search_album;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") +
                    ";release_date|" + data +
                    ";description|" + descricao +
                    ";ratingAVG|" + Integer.toString(Math.round(rating)) +
                    ";countMusic|" + musicaCount +
                    ";music|" + musicas +
                    ";countReview|" + reviewCount +
                    ";reviews|" + reviews +
                    ";id|" + map.get("id") + ";status|accepted;";
        } else {
            System.out.println("nao econtrei o album");
            return "type|search_album;album_name|" + map.get("album_name") + ";id|" + map.get("id") + ";status|notfound";
        }
    }

    public String date2String(Object data) {
        System.out.println(data);
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(data);
    }

    public String detalhesArtista(HashMap<String, String> map) throws SQLException {
        Artista artista = new Artista(map.get("artist_name"), "", "");
        if (checkArtist(artista)) {
            //se existir
            //buscar informacao
            PreparedStatement st = connection.prepareStatement("select idartista, bibliografia, data_nasc from artista where nome = ?");
            st.setString(1, artista.nome);
            ResultSet rs = st.executeQuery();
            rs.next();
            int idArtista = rs.getInt(1);
            String bibliografia = rs.getString(2);
            String data = date2String(rs.getObject(3));
            //buscar albuns
            PreparedStatement stAlbuns = connection.prepareStatement("select nome from album where artista_idartista in (select idartista from artista where nome = ?)");
            stAlbuns.setString(1, map.get("artist_name"));
            ResultSet rsAlbuns = stAlbuns.executeQuery();
            ArrayList<String> albums = new ArrayList<>();
            while (rsAlbuns.next()) {
                albums.add(rsAlbuns.getString(1));
            }
            String todos = "";
            for (String temp : albums) {
                todos = todos.concat(temp + "+");
            }
            if (todos.length() > 0)
                todos = todos.substring(0, todos.length() - 1);

            return "type|artist_details;artist_name|" + map.get("artist_name") + ";bibliografia|" + bibliografia + ";count|" + albums.size() + ";albums|" + todos + ";birth_date|" + data + ";id|" + map.get("id") + ";status|accepted;";
        } else {
            return "type|artist_details;artist_name|" + map.get("artist_name") + ";id|" + map.get("id") + ";status|notfound;";
        }
    }

    public String addCompositores(HashMap<String, String> map) throws SQLException {
        String tempTodos = map.get("compositor_name");
        String[] artista = tempTodos.split(",");
        int acum = 0;
        for (String temp : artista) {
            if (checkArtist(new Artista(map.get("compositor_name"), "", ""))) {
                acum += 1;
            }
        }
        if (acum != artista.length) { //caso nao encontre
            return "type|add_compositor;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";compositor_name|" + map.get("compositor_name") + ";id|" + map.get("id") + ";status|notfound;";
        }
        //caso exista os compositores
        PreparedStatement check = connection.prepareStatement("select idmusica from musica where nome = ? and album_idalbum = (select idalbum from album where nome = ? and artista_idartista = (select idartista from artista where nome = ?))");
        check.setString(1, map.get("music_name"));
        check.setString(2, map.get("album_name"));
        check.setString(3, map.get("artist_name"));
        ResultSet rs = check.executeQuery();
        if (!rs.next()) {
            return "type|add_compositor;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";compositor_name|" + map.get("compositor_name") + ";id|" + map.get("id") + ";status|notfound;";
        }
        //caso exista a musica
        int idmusica = rs.getInt(1);
        PreparedStatement st = connection.prepareStatement("insert into compositor_musica values (?, ?)");
        st.setInt(1, idmusica);
        st.setInt(2, getIdArtista(new Artista(map.get("artist_name"), "", "")));
        st.executeUpdate();
        return "type|add_compositor;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";compositor_name|" + map.get("compositor_name") + ";id|" + map.get("id") + ";status|accepted;";
    }

    public String detalhesMusica(HashMap<String, String> map) throws SQLException {
        PreparedStatement check = connection.prepareStatement("select idmusica from musica where nome = ? and album_idalbum = (select idalbum from album where nome = ? and artista_idartista = (select idartista from artista where nome = ?))");
        check.setString(1, map.get("music_name"));
        check.setString(2, map.get("album_name"));
        check.setString(3, map.get("artist_name"));
        ResultSet rs = check.executeQuery();

        if (!rs.next()) { //caso nao exista a musica
            return "type|music_details;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|notfound";
        } //caso exista
        //comp
        StringBuilder comp = new StringBuilder();
        String tempComp;
        int idmusica = rs.getInt(1);
        PreparedStatement st = connection.prepareStatement("select nome from artista where idartista in (select artista_idartista from compositor_musica where musica_idmusica = ?)");
        st.setInt(1, idmusica);
        ResultSet rsComp = st.executeQuery();
        while (rsComp.next()) { //existem compositores associados
            tempComp = rsComp.getString(1);
            comp.append(tempComp).append(",");
        }
        if (comp.length() > 0) {
            comp.deleteCharAt(comp.length() - 1);
        }
        //letra
        String letra;
        PreparedStatement stLetra = connection.prepareStatement("select letra from letra where musica_idmusica = ?");
        stLetra.setInt(1, idmusica);
        ResultSet rsLetra = stLetra.executeQuery();
        rsLetra.next();
        letra = rsLetra.getString(1);

        return "type|music_details;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";lyrics|" + letra + ";compositores|" + comp + ";id|" + map.get("id") + ";status|accepted";
    }

    public String addPlaylist(HashMap<String, String> map) throws SQLException {
        PreparedStatement st = connection.prepareStatement("insert into playlist values(?,?)");
        st.setString(1, map.get("playlist_name"));
        try {
            ResultSet rs = st.executeQuery();
            PreparedStatement st1 = connection.prepareStatement("insert into utilizador_playlist values(?,?)");
            st1.setString(1, map.get("username"));
            st1.setString(2, map.get("playlist_name"));
            st1.executeUpdate();
            return "type|new_playlist;public|" + map.get("public") + ";playlist_name|" + map.get("playlist_name") + ";id|" + map.get("id") + ";status|accepted";
        } catch (SQLException e) {
            // Other SQL Exception
            if (e.getSQLState().equals("23505")) {
                //caso ja exista
                return "type|new_playlist;public|" + map.get("public") + ";playlist_name|" + map.get("playlist_name") + ";id|" + map.get("id") + ";status|rejected;";
            } else {
                System.out.println("erro no catch, dunno");
                return "type|new_playlist;public|" + map.get("public") + ";playlist_name|" + map.get("playlist_name") + ";id|" + map.get("id") + ";status|rejected;";
            }
        }
    }

//    public String detalhesPlaylist(HashMap<String, String> map) throws SQLException {
//        PreparedStatement st = connection.prepareStatement("select nome from musica where idmusica = (select musica_idmusica from musica_playlist where playlist_nome = ?)");
//        st.setString(1, map.get("playlist_name"));
//        ResultSet rs = st.executeQuery();
//
//        while(rs.next()) {
//
//        }
//    }

    public String editArtista(HashMap<String, String> map) throws SQLException {
        if(map.containsKey("bibliografia")) { //caso ja seja para editar
            if(!map.get("bibliografia").isEmpty()) { //caso tenha feito alteracoes na bibliografia
                if(!map.get("birth_date").isEmpty()) { //caso tenha feito alteracoes na data e na bibliografia
                    PreparedStatement st = connection.prepareStatement("update artista set bibliografia = ? and data_nasc = ? where nome = ?");
                    st.setString(1, map.get("bibliografia"));
                    st.setObject(2, string2Date(map.get("birth_date")));
                    st.setString(3, map.get("artist_name"));
                    st.executeUpdate();
                } //caso mude apenas a bibliografia
                PreparedStatement st = connection.prepareStatement("update artista set bibliografia = ? where nome = ?");
                st.setString(1, map.get("bibliografia"));
                st.setString(2, map.get("artist_name"));
                st.executeUpdate();
            } if(!map.get("birth_date").isEmpty()) {
                PreparedStatement st = connection.prepareStatement("update artista set data_nasc = ? where nome = ?");
                st.setObject(1, string2Date(map.get("birth_date")));
                st.setString(2, map.get("artist_name"));
            }
            return "type|album_edit;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|accepted;";
        } else { //para pedir dados
            if(checkArtist(new Artista(map.get("artist_name"), "", ""))){
                String resposta;
                resposta = detalhesArtista(map);
                resposta = resposta.substring(0, resposta.indexOf("accepted;")).concat("pending;");
                return resposta;
            }
            return "type|artist_edit;artist_name|"+map.get("artist_name")+";id|"+map.get("id")+";status|notfound;";
        }
    }

    public String editAlbum(HashMap<String, String> map) throws SQLException {
        if(map.containsKey("genre")) { //caso seja para editar
            if(!map.get("genre").isEmpty()) { //alterou o genre
                PreparedStatement st = connection.prepareStatement("update album set genero = ? where nome = ? and artista_idartista = (select idartista from artista where nome = ?) returning idalbum");
                st.setString(1, map.get("genre"));
                st.setString(2, map.get("album_name"));
                st.setString(3, map.get("artist_name"));
                ResultSet rs = st.executeQuery();
                rs.next();
                int idalbum = rs.getInt(1);
                //genero
                PreparedStatement stGenero = connection.prepareStatement("insert int genero values(?)");
                stGenero.setString(1, map.get("genre"));
                stGenero.executeUpdate();
                PreparedStatement st2 = connection.prepareStatement("update album_genero set genero_nome = ? where album_idalbum = ?");
                st2.setString(1, map.get("genre"));
                st2.setInt(2, idalbum);
                st2.executeUpdate();
            }
            if(!map.get("release_date").isEmpty()) { //alterar a data
                PreparedStatement stDate = connection.prepareStatement("update album set data = ? where where nome = ? and artista_idartista = (select idartista from artista where nome = ?)");
                stDate.setObject(1, string2Date(map.get("release_date")));
                stDate.setString(2, map.get("album_name"));
                stDate.setString(3, map.get("artist_name"));
                stDate.executeUpdate();
            }
            if(!map.get("description").isEmpty()){
                PreparedStatement stDesc = connection.prepareStatement("update album set descricao = ? where where nome = ? and artista_idartista = (select idartista from artista where nome = ?)");
                stDesc.setString(1, map.get("description"));
                stDesc.setString(2, map.get("album_name"));
                stDesc.setString(3, map.get("artist_name"));
                stDesc.executeUpdate();
            }
            return "type|artist_edit;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|accepted;";

        } else { //para pedir dados
            if(checkAlbumArtista(new Album(map.get("artist_name"), "", map.get("album_name"), (float)0, "", "", ""),
                    new Artista(map.get("artist_name"), "", ""))) {
                String resposta;
                resposta = detalhesAlbum(map);
                resposta = resposta.substring(0, resposta.indexOf("accepted;")).concat("pending;");
                return resposta;
            }
            return "type|album_edit;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|notfound;";
        }
    }

    public String editMusica(HashMap<String, String> map) throws SQLException {
        if(map.containsKey("lyrics")){ //para alterar
            if(!map.get("lyrics").isEmpty()) { //alterar lyrics
                PreparedStatement stLetra = connection.prepareStatement("update letra set letra = ? where musica_idmusica = (" +
                        "select idmusica from musica where nome = ? and album_idalbum = (" +
                        "select idalbum from album where nome = ? and artista_idartista = (" +
                        "select idartista from artista where nome = ?)))");
                stLetra.setString(1, map.get("lyrics"));
                stLetra.setString(2, map.get("music_name"));
                stLetra.setString(3, map.get("album_name"));
                stLetra.setString(4, map.get("artist_name"));
                stLetra.executeUpdate();
            }
            if(!map.get("compositor_name").isEmpty()) { //alterar os compositores
                String tempTodos = map.get("compositor_name");
                String[] artista = tempTodos.split(",");
                int acum = 0;
                for (String temp : artista) {
                    if (checkArtist(new Artista(map.get("compositor_name"), "", ""))) {
                        acum += 1;
                    }
                }
                if (acum != artista.length) { //caso nao encontre
                    return "type|music_edit;artist_name|" + map.get("artist_name") + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";compositor_name|" + map.get("compositor_name") + ";id|" + map.get("id") + ";status|notfound;";
                }
                //caso existam os compositores
                PreparedStatement stComp = connection.prepareStatement("delete from compositor_musica where musica_idmusica = (" +
                        "select idmusica from musica where nome = ? and album_idalbum = (" +
                        "select idalbum from album where nome = ? and artista_idartista = (" +
                        "select idartista from artista where nome = ?)))");
                stComp.setString(1, map.get("music_name"));
                stComp.setString(2, map.get("album_name"));
                stComp.setString(3, map.get("artist_name"));
                stComp.executeUpdate();
                addCompositores(map);
            }
            return "type|music_edit;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|accepted;";
        } else { //pedido
            if(checkMusicAlbumArtista(new Musica(map.get("music_name"), map.get("artist_name"), map.get("album_name"), ""),
                    new Album(map.get("artist_name"), "", map.get("album_name"), (float)0, "", "", ""),
                    new Artista(map.get("artist_name"), "", ""))) {
                String resposta;
                resposta = detalhesMusica(map);
                resposta = resposta.substring(0, resposta.indexOf("accepted;")).concat("pending;");
                return resposta;
            }
            return "type|music_edit;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|notfound;";
        }
    }

    public String deleteArtista(HashMap<String, String> map) throws SQLException {
        if(!checkArtist(new Artista(map.get("artist_name"), "", ""))) {
            return "type|delete_artist;artist_name|"+map.get("artist_name")+";id|"+map.get("id")+";status|notfound";
        }
        PreparedStatement st = connection.prepareStatement("delete from artista where nome = ?");
        st.setString(1, map.get("artist_name"));
        st.executeUpdate();
        return "type|delete_artist;artist_name|"+map.get("artist_name")+";id|"+map.get("id")+";status|accepted";
    }

    public String deleteAlbum(HashMap<String, String> map) throws SQLException {
        if(!checkAlbumArtista( new Album(map.get("artist_name"), "", map.get("album_name"), (float)0, "", "", ""), new Artista(map.get("artist_name"), "", ""))) {
            return "type|delete_album;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|notfound";
        }
        PreparedStatement st = connection.prepareStatement("delete from album where nome = ? and artista_idartista = (select idartista from artista where nome = ?)");
        st.setString(1, map.get("album_name"));
        st.setString(2, map.get("artist_name"));
        st.executeUpdate();
        return "type|delete_album;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|accepted";
    }

    public String deleteMusica(HashMap<String, String> map) throws SQLException {
        if(!checkMusicAlbumArtista(new Musica(map.get("music_name"), map.get("artist_name"), map.get("album_name"), ""), new Album(map.get("artist_name"), "", map.get("album_name"), (float)0, "", "", ""), new Artista(map.get("artist_name"), "", ""))) {
            return "type|delete_music;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|notfound";
        }
        PreparedStatement st = connection.prepareStatement("delete from musica where nome = ? and album_idalbum = (select idalbum from album where nome = ? and artista_idartista = (select idartista from artista where nome = ?))");
        st.setString(1, map.get("music_name"));
        st.setString(2, map.get("album_name"));
        st.setString(3, map.get("artist_name"));
        st.executeUpdate();
        return "type|delete_music;artist_name|"+map.get("artist_name")+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|accepted";
    }
}
