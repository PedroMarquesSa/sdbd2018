import java.io.*;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;

public class Ficheiros implements Serializable{
    //FICHEIROS DE OBJETO

        private int id;
        private int firstOne;
        public Ficheiros(int id) {
            this.id = id;
            //this.firstOne = 0; //ATENCAO ERRO PODE VIR DAQUI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }

        public void criaFicheiroObjeto(String aux, int id) {
            try {
                FileOutputStream f = new FileOutputStream(new File(aux + Integer.toString(id) + ".obj")); //guardar contas (usernames, password, status)
                //ObjectOutputStream ocontas = new ObjectOutputStream(fcontas);
                //ocontas.close();
                f.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Abre e carrega users para ArrayList para consultar
        public ArrayList<User> buscaUsers() {
            ObjectInputStream iS = null;
            try {
                iS = new ObjectInputStream(new FileInputStream("Contas"+Integer.toString(id)+".obj"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //array de users
            ArrayList<User> users = new ArrayList<>();
            try {
                if (iS != null) {
                    users = (ArrayList<User>) iS.readObject();
                    iS.close();
                } else {
                    System.out.println("Users esta vazio");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            //imprime users
            System.out.println("<=====================><=====================><=====================>");
            for(User aux : users) {
                System.out.println(aux.username+"->"+aux.password+"->"+aux.what+"->"+aux.status);
            }
            System.out.println("<=====================><=====================><=====================>");
            return users;
        }

        //encontra o User especifico no array
        public User encontraUser(ArrayList<User> aux, User user) {
            User temp = null;
            for(User i : aux) {
                if(i.username.equals(user.username)) {
                    temp = i;
                    return temp;
                }
            }
            return temp;
        }


        public void makeAdmin(User user, ArrayList<User> users){
            int i = 0;
            for(User aux:users) {
                if(user.username.equals(aux.username)){
                    users.get(i).what = "admin";
                    System.out.println("Ficou admin");
                    return ;
                }
                i++;
            }
        }

        public boolean checkEmpty(ArrayList<User> users) {
            return users.isEmpty();
        }

        public boolean checkEmptyBiblioteca(ArrayList<Artista> artistas) {
            return artistas.isEmpty();
        }

        //atualiza User - lista de utilizadores
        public void updateUserList(ArrayList<User> aux) {
            ObjectOutputStream oS = null;
            try {
                oS = new ObjectOutputStream(new FileOutputStream("Contas"+Integer.toString(id)+".obj"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (oS != null) {
                    oS.writeObject(aux);
                    System.out.println("\natualizei lista:");
                    oS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //imprime users
            for(User user : aux) {
                System.out.println(user.username+"->"+user.password+"->"+user.what+"->"+user.status);
            }
            System.out.println("<=====================><=====================><=====================>");
        }

        public boolean checkUser(ArrayList<User> aux, String username) {
            for (User anAux : aux) {
                if (anAux.username.equals(username))
                    return true; //ja existe alguem com este username
            }
            return false; //livre para utilizar
        }

        public int checkPassword(ArrayList<User> aux, User user) {
            int resposta = 0;
            for (User anAux : aux) {
                if (anAux.username.equals(user.username)) {
                    resposta = 1;
                    if (anAux.password.equals(user.password)) {
                        return 2; //certo
                    }
                }
            }
            return resposta;
        }

        //user fica online
        public void turnOnline(ArrayList<User> aux, User user) {
            int i=0;
            for (User anAux : aux) {
                if (anAux.username.equals(user.username)) {
                    aux.get(i).status = "online";
                    return;
                }
                i++;
            }
        }

        //Registo -> devolve a string a enviar pelo server
        public String registerUser(HashMap<String, String> map) {
            String tempResposta;
            boolean first = false;
            User user = new User(map.get("username"), map.get("password"), "null", "normal"); //status-> null (ato de registar)
            ArrayList<User> aux;
            aux = buscaUsers();
            if(checkEmpty(aux))
                first = true;
            //SINCRONIZACAO ??????????
            if(!checkUser(aux, user.username)){ //caso possa utilizar esse nome
                aux.add(user);
                if(first){
                    makeAdmin(user, aux); //caso seja o primeiro
                }
                updateUserList(aux); //carrega para o ficheiro
                tempResposta = "type|register;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
            } else { //username ja usado (DAR UPDATE DEPOIS)
                System.out.println("username ja usado");
                tempResposta = "type|register;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|rejected";
            }
            return tempResposta;
        }

        //Login -> devolve a string a enviar pelo server
        public String loginUser(HashMap<String, String> map) {
            String tempResposta = null;
            User user = new User(map.get("username"), map.get("password"), "accepted", "normal"); //status -> accepted
            ArrayList<User> aux;
            aux = buscaUsers();
            int temp = checkPassword(aux, user);
            if(temp == 2){ //existe essa conta e a pass esta certa
                tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
                turnOnline(aux, user);
                updateUserList(aux);
            } else if(temp == 1) { //username ja usado, pass errada
                System.out.println("Username ja usado");
                tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongpassword";
            } else if(temp == 0){
                System.out.println("Username nao existe");
                tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongusername";
            }
            return tempResposta;
        }

        //DAR PERMISSOES DE EDITOR
        public String tornarEditor(HashMap<String, String> map) {
            String tempResposta = null;
            User user = new User(map.get("username"), map.get("password"), "accepted", map.get("what")); //status -> accepted
            User userDest = new User(map.get("usernameDest"), "null", "null", map.get("null")); //user que quero promover
            int i=0; //iterador para modificar
            ArrayList<User> aux;
            aux = buscaUsers();
            user = encontraUser(aux, user);
            userDest = encontraUser(aux, userDest);
            if(user!=null){ // caso exista o user a tentar promover
                if(userDest!=null){ //caso exista o user a ser promovido
                    if (user.what.equals("admin") || user.what.equals("editor")) { //caso o user a querer promover seja admin ou editor
                        for(User temp : aux) {
                            if (temp.username.equals(userDest.username)){
                                aux.get(i).what = "editor";
                                updateUserList(aux);
                                tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|accepted;";
                                return tempResposta;
                            }
                            i++;
                        }
                    }
                    //caso o user nao tenha permissao
                    tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|rejected;";
                    return tempResposta;
                }
                //caso nao exista o user a ser promovido
                tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusernameDest;";
                return tempResposta;
            }
            //caso nao exista o user a ser promovido
            tempResposta = "type|promote;username|"+map.get("username")+";password|"+map.get("password")+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusername;";
            return tempResposta;
        }

        //MUSICA
        public Biblioteca buscaBiblioteca() {
            Biblioteca biblioteca = new Biblioteca(); //caso nao tenha nada
            ObjectInputStream iS = null;
            try {
                iS = new ObjectInputStream(new FileInputStream("Biblioteca"+Integer.toString(id)+".obj"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //extrair
            try {
                if (iS != null) {
                    biblioteca = (Biblioteca) iS.readObject();
                    iS.close();
                } else {
                    System.out.println("Biblioteca esta vazia");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return biblioteca;
        }

        //atualiza biblioteca de musicas
        public void updateBiblioteca(Biblioteca biblioteca) {
            ObjectOutputStream oS = null;
            try {
                oS = new ObjectOutputStream(new FileOutputStream("Biblioteca"+Integer.toString(id)+".obj"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (oS != null) {
                    oS.writeObject(biblioteca);
                    System.out.println("\natualizei biblioteca");
                    oS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(">==================>>==================>>==================>>==================>");
            System.out.println(">==================>>==================>>==================>>==================>");
            for(Artista temp:biblioteca.artistas) {
                System.out.println("Artista:");
                System.out.println(temp.nome);
                for(Album tempAlbum:temp.albums) {
                    System.out.println("Album:");
                    System.out.println(tempAlbum.nome);
                    System.out.println("Musicas:");
                    for(Musica musica:tempAlbum.musicas) {
                        System.out.println(musica.nome);
                    }
                    System.out.println();
                }
                System.out.println();
            }
            System.out.println(">==================>>==================>>==================>>==================>");
            System.out.println(">==================>>==================>>==================>>==================>");
        }

        public boolean checkArtist(Biblioteca biblioteca, Artista artista) {
            for(Artista aux:biblioteca.artistas) {
                if(aux.nome.equals(artista.nome)){
                    return true; //caso exista esse artista
                }
            }
            return false; //casp NAO exista esse artista
        }

        public boolean checkPermission(User user){
            ArrayList<User> aux = buscaUsers();
            for(User temp:aux) {
                if(temp.username.equals(user.username)) {
                    return (temp.what.equals("editor") || temp.what.equals("admin"));
                }
            }
            return false;
        }

        public String addArtista(HashMap<String, String> map) {
            String tempResposta;
            Biblioteca biblioteca = new Biblioteca();
            ArrayList<Album> albums = new ArrayList<>();
            Artista artista = new Artista(map.get("artist_name"), map.get("description"), map.get("birth_date"), albums);        //verificar se tem permissoes para adicionar
            User user = new User(map.get("username"), "null", "null", "null");
            if(!checkPermission(user)){
                return "type|new_artist;username|"+map.get("username")+"artist_name|"+artista.nome+";id|"+map.get("id")+";status|nopermission";
            }
            biblioteca = buscaBiblioteca();
            if(!checkEmptyBiblioteca(biblioteca.artistas)){
                if(!checkArtist(biblioteca, artista)) { //caso nao exista o artista
                    biblioteca.artistas.add(artista);
                    updateBiblioteca(biblioteca);
                    tempResposta = "type|new_artist;username|"+map.get("username")+"artist_name|"+artista.nome+";id|"+map.get("id")+";status|accepted";
                }
                else { //caso ja exsita o artista
                    System.out.println("artista ja exista");
                    tempResposta = "type|new_artist;username|"+map.get("username")+"artist_name|"+artista.nome+";id|"+map.get("id")+";status|rejected";
                }
            } else { //caso a biblioteca esteja vazia
                biblioteca.artistas.add(artista);
                updateBiblioteca(biblioteca);
                tempResposta = "type|new_artist;username|"+map.get("username")+"artist_name|"+artista.nome+";id|"+map.get("id")+";status|accepted";
            }
            return tempResposta;
        }
        public String addAlbum(HashMap<String, String> map) {
            String tempResposta = null;
            Biblioteca biblioteca = new Biblioteca();
            ArrayList<Album> albums = new ArrayList<>();
            Artista artista = new Artista(map.get("artist_name"), map.get("null"), map.get("null"), albums);
            //verificar se tem permissoes para adicionar
            User user = new User(map.get("username"), "null", "null", "null");
            if(!checkPermission(user)){
                return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|nopermission";
            }
            ArrayList<Review> reviews = new ArrayList<>();
            ArrayList<Musica> musicas = new ArrayList<>();
            Album album = new Album(map.get("artist_name"), map.get("description"), map.get("album_name"), (float)0, reviews, musicas);
            biblioteca = buscaBiblioteca();
            if(!checkEmptyBiblioteca(biblioteca.artistas)) { //a biblioteca nao esta vazia
                if (!checkArtist(biblioteca, artista)) { //caso nao exista o artista
                    artista.albums.add(album);
                    biblioteca.artistas.add(artista);
                    updateBiblioteca(biblioteca);
                    System.out.println("\nnao existe o artista mas adicionei\n");
                    return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|accepted";
                }
                if(checkAlbum(biblioteca, album)){ //caso o album ja exista
                    System.out.println("\nja existe album\n");
                    return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|rejected";
                } //caso o album ainda nao exista
                int i = 0;
                for(Artista temp:biblioteca.artistas) {
                    if(temp.nome.equals(artista.nome)) {
                        biblioteca.artistas.get(i).albums.add(album);
                        updateBiblioteca(biblioteca);
                        return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|accepted";
                    }
                    i++;
                }
            }
            else { //a biblioteca esta vazia
                biblioteca.artistas.add(artista);
                biblioteca.artistas.get(0).albums.add(album);
                updateBiblioteca(biblioteca);
                return "type|new_album;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+album.nome+";id|"+map.get("id")+";status|accepted";
            }
            return tempResposta;
        }

        //adiciona musica
        public String addMusica(HashMap<String, String> map) {
            Biblioteca biblioteca = buscaBiblioteca();
            ArrayList<Album> albums = new ArrayList<>();
            Artista artista = new Artista(map.get("artist_name"), map.get("null"), map.get("null"), albums);
            //verificar se tem permissoes para adicionar
            User user = new User(map.get("username"), "null", "null", "null");
            if(!checkPermission(user)){
                return "type|new_music;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|nopermission";
            }
            ArrayList<Review> reviews = new ArrayList<>();
            ArrayList<Musica> musicas = new ArrayList<>();
            Album album = new Album(artista.nome, "", map.get("album_name"), (float) 0, reviews, musicas);
            Musica musica = new Musica(map.get("music_name"), artista.nome, album.nome);
            //verificar se o artista existe
            if(!checkEmptyBiblioteca(biblioteca.artistas)) { //a biblioteca nao esta vazia
                if (!checkArtist(biblioteca, artista)) { //caso nao exista o artista
                    album.musicas.add(musica);
                    artista.albums.add(album);
                    biblioteca.artistas.add(artista);
                    updateBiblioteca(biblioteca);
                    System.out.println("\nnao existe o artista mas adicionei\n");
                    return "type|new_music;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|accepted";
                } //caso exista o artista
                if(!checkAlbum(biblioteca, album)) { //caso o album nao exista
                    int i = 0;
                    for (Artista tempArtista : biblioteca.artistas) {
                        if (tempArtista.nome.equals(artista.nome)) {
                            album.musicas.add(musica);
                            biblioteca.artistas.get(i).albums.add(album);
                            updateBiblioteca(biblioteca);
                            System.out.println("\nnao existe o album mas adicionei\n");
                            return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|accepted";
                        }
                        i++;
                    }
                } else {//caso o album exista
                    if(checkMusic(biblioteca, musica)) { //caso ja exista a musica
                        return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|rejected";
                    } else{
                        int i = 0;
                        int j = 0;
                        for (Artista tempArtista : biblioteca.artistas) {
                            if (tempArtista.nome.equals(artista.nome)) {
                                for(Album tempAlbum:biblioteca.artistas.get(i).albums) {
                                    if(tempAlbum.nome.equals(album.nome)) {
                                        biblioteca.artistas.get(i).albums.get(j).musicas.add(musica);
                                        updateBiblioteca(biblioteca);
                                        System.out.println("\nexiste o album e adicionei\n");
                                        return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|accepted";
                                    }
                                    j++;
                                }
                                biblioteca.artistas.get(i).albums.add(album);
                                updateBiblioteca(biblioteca);
                                System.out.println("\nnao existe o album mas adicionei\n");
                                return "type|new_music;username|" + user.username + ";artist_name|" + artista.nome + ";album_name|" + map.get("album_name") + ";music_name|" + map.get("music_name") + ";id|" + map.get("id") + ";status|accepted";
                            }
                            i++;
                        }
                    }
                }
            } //caso esteja vazia
            else {
                biblioteca.artistas.add(artista);
                biblioteca.artistas.get(0).albums.add(album);
                biblioteca.artistas.get(0).albums.get(0).musicas.add(musica);
                updateBiblioteca(biblioteca);
                System.out.println("a libraria estava vazia");
                return "type|new_music;username|"+user.username+";artist_name|"+artista.nome+";album_name|"+map.get("album_name")+";music_name|"+map.get("music_name")+";id|"+map.get("id")+";status|accepted";
            }
            return "";
        }

        //sprocura por artista e devolve albuns
        public String searchArtista(HashMap<String, String> map){
            int count = 0; //contador de musicas (redundante?????)
            StringBuilder albunsString = new StringBuilder();
            Biblioteca biblioteca = buscaBiblioteca();
            ArrayList<Album> albuns = new ArrayList<>();
            Artista artista = new Artista(map.get("artist_name"), "", "", albuns);
            if(!checkEmptyBiblioteca(biblioteca.artistas)) { //havendo artistas
                if(checkArtist(biblioteca, artista)) {
                    for(Artista temp:biblioteca.artistas) {
                        if(artista.nome.equals(temp.nome)) {
                            //encontra o artista
                            count += temp.albums.size();
                            for(Album tempAlbum:temp.albums){
                                albunsString.append(tempAlbum.nome).append("+");
                            }
                        }
                    }
                } else {
                    return "type|search_artist;artist_name|"+artista.nome+";id|"+map.get("id")+";status|notfound;";
                }
                if(count>0) { //se existirem musicas
                    albunsString.substring(0, albunsString.length() - 1);
                    return "type|search_artist;artist_name|"+artista.nome+";count|"+Integer.toString(count)+";albums|"+albunsString+":id|"+map.get("id")+";status|accepted";
                } else { //existe o artista mas nao tem musicas associadas
                    return "type|search_artist;artist_name|"+artista.nome+";id|"+map.get("id")+";status|empty";
                }
            } else { //artista nao existe
                return "type|search_artist;artist_name|"+artista.nome+";id|"+map.get("id")+";status|notfound;";
            }
        }

        //procurar por album
        public String searchAlbum(HashMap<String, String> map) {
            Biblioteca biblioteca = buscaBiblioteca();
            if(!checkEmptyBiblioteca(biblioteca.artistas)) { //havendo artistas
                for(Artista artistaTemp:biblioteca.artistas) {
                    for(Album albumTemp:artistaTemp.albums) {
                        if(albumTemp.nome.equals(map.get("album_name"))) {
                            return "type|search_album;artist_name|"+albumTemp.Artista+";album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|accepted;";
                        }
                    }
                }
            }
            return "type|search_album;album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|notfound";
        }

        //detalhes sobre album
        public String detalhesAlbum(HashMap<String, String> map) {
            Biblioteca biblioteca = buscaBiblioteca();
            if(!checkEmptyBiblioteca(biblioteca.artistas)) { //havendo artistas
                for (Artista artistaTemp : biblioteca.artistas) {
                    if (map.get("artist_name").equals(artistaTemp.nome)) { //encontrou artista
                        for (Album albumTemp : artistaTemp.albums) {
                            if (albumTemp.nome.equals(map.get("album_name"))) {
                                float review = ratingAVG(albumTemp);
                                StringBuilder musicas = new StringBuilder();
                                StringBuilder reviews = new StringBuilder();
                                //busca todas as musicas presentes no album
                                int i = 0;
                                for(Musica musicaTemp:albumTemp.musicas) {
                                    musicas.append(albumTemp.musicas.get(i).nome).append("+");
                                    i++;
                                }
                                //busca todas as reviews presentes no album
                                for(Review reviewTemp:albumTemp.reviews) {
                                    reviews.append(Integer.toString(Math.round(reviewTemp.rating))).append("+").append(reviewTemp.descricao).append("*");
                                }
                                if(reviews.length()>0) //caso haja alguma review
                                    reviews.deleteCharAt(reviews.length()-1);
                                if(musicas.length()>0) //caso haja alguma musica
                                    musicas.deleteCharAt(musicas.length()-1);
                                return "type|search_album;artist_name|" + albumTemp.Artista + ";album_name|" + map.get("album_name") +
                                        ";description|"+albumTemp.descricao+
                                        ";ratingAVG|"+Integer.toString(Math.round(review))+
                                        ";countMusic|"+albumTemp.musicas.size()+
                                        ";music|"+musicas+
                                        ";countReview|"+albumTemp.reviews.size()+
                                        ";reviews|"+reviews+
                                        ";id|" + map.get("id") + ";status|accepted;";
                            }
                        }
                    }
                } //nao encontra o artista
            }
            return "type|search_album;album_name|"+map.get("album_name")+";id|"+map.get("id")+";status|notfound";
        }

        //escreve uma review
        public String writeReview(HashMap<String, String> map) {
            Biblioteca biblioteca = buscaBiblioteca();
            User user = new User(map.get("username"), "", "", ""); //user que quer escrever
            if(checkPermission(user)) { //pode escever
                if(!checkEmptyBiblioteca(biblioteca.artistas)) { //havendo artistas
                    int j = 0;
                    for(Artista artistaTemp:biblioteca.artistas) {
                        if(artistaTemp.nome.equals(map.get("artist_name"))) {
                            int i = 0; //para poder alterar o array
                            for(Album albumTemp:artistaTemp.albums) {
                                if(albumTemp.nome.equals(map.get("album_name"))) {
                                    Review review = new Review(map.get("artist_name"), map.get("album_name"), map.get("critic"), Float.parseFloat(map.get("rating")), user.username);
                                    biblioteca.artistas.get(j).albums.get(i).reviews.add(review);
                                    updateBiblioteca(biblioteca);
                                    return "type|album_critic;username|"+user.username+"album_name|OK Computer;critic|"+map.get("critic")+";rating|"+map.get("rating")+";id|"+map.get("id")+";status|accepted";
                                }
                                i++;
                            }
                        }
                        j++;
                    }
                }
                return "type|album_critic;username|"+user.username+"album_name|OK Computer;critic|"+map.get("critic")+";rating|"+map.get("rating")+";id|"+map.get("id")+";status|notfound";
            } else {
                return "type|album_critic;username|"+user.username+"album_name|OK Computer;critic|"+map.get("critic")+";rating|"+map.get("rating")+";id|"+map.get("id")+";status|nopermission";
            }

        }

        public float ratingAVG(Album album) {
            float aux = 0;
            for(Review review:album.reviews) {
                aux += review.rating;
            }
            return (float)aux/album.reviews.size();
        }
        public boolean checkMusic(Biblioteca biblioteca, Musica musica) {
            for(Artista tempArtista:biblioteca.artistas) {
                if(tempArtista.nome.equals(musica.Artista)) {
                    for(Album tempAlbum:tempArtista.albums) {
                        if(tempAlbum.nome.equals(musica.Album)) {
                            for(Musica tempMusica:tempAlbum.musicas) {
                                if(musica.nome.equals(tempMusica.nome)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        //check se existe
        public boolean checkAlbum(Biblioteca biblioteca, Album album) {
            for(Artista tempArtista:biblioteca.artistas) {
                for(Album tempAlbum:tempArtista.albums) {
                    if(tempAlbum.nome.equals(album.nome)) {
                        return true;
                    }
                }
            }

            return false;
        }
        //converte
        private HashMap<String, String> string2Hashmap(String message) {
            HashMap<String, String> aux = new HashMap<>();
            String[] temp = message.split(";");
            String[] each;

            for (int i=0; i<temp.length; i++) {
                each = temp[i].split("\\|");
                aux.put(each[0], each[1]);
            }
            return aux;
        }

        public HashMap<String, String> packet2Hashmap(DatagramPacket packet) {
            HashMap<String, String> aux = new HashMap<>();
            String message = new String(packet.getData(), 0, packet.getLength());
            String[] temp = message.split(";");
            String[] each;

            for (int i=0; i<temp.length; i++) {
                each = temp[i].split("\\|");
                aux.put(each[0], each[1]);
            }
            return aux;
        }
}


