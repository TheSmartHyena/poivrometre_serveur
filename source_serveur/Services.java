import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.google.gson.*;

import java.sql.*;
import java.util.Date;

@Path("/services")
public class Services {

    static Connection conn;

    /**
     * Méthode qui permet de tester depuis un client la connexion à ce serveur
     * @return "test" pour confirmer que l'appel a bien fonctionné
     */
    @GET
    @Path("/test")
    public String test(){
        return "test";
    }

    /**
     * Méthode de connexion qui permet de se connecter à un compte existant
     * @param json de type String qui se présente sous la forme { pseudo: "", mdp: "" }
     * @return une "clef" de connexion qui permet d'identifier l'utilisateur lors des futurs appels
     */
    @POST
    @Path("/connect")
    @Consumes(MediaType.APPLICATION_JSON)
    public String connect(String json){
        Gson creator = new Gson();
        Connexion connexion = creator.fromJson(json, Connexion.class);
        try {
            Connection con = Services.getCO();
            PreparedStatement prepared2 = con.prepareStatement("SELECT pseudo FROM Compte where pseudo=? and pwd=?");
            prepared2.setString(1,connexion.getPseudo());
            prepared2.setString(2,connexion.getMdp());
            ResultSet rs2 = prepared2.executeQuery();
            String key = "";
            if(rs2.next()) {
                key = Services.getAlphaNumericString(20);
                PreparedStatement prepared;
                prepared = con.prepareStatement("select * from Utilisateur where pseudo =?");
                prepared.setString(1,connexion.getPseudo());
                ResultSet rs3 = prepared.executeQuery();
                if(!rs3.next()){
                    prepared = con.prepareStatement("INSERT INTO `Utilisateur`(`pseudo`, `IDUser`) VALUES (?,?)");
                    prepared.setString(1,connexion.getPseudo());
                    prepared.setString(2,key);
                    prepared.execute();
                }else{
                    prepared = con.prepareStatement("update Utilisateur set IDUser = ? where pseudo = ?");
                    prepared.setString(1,key);
                    prepared.setString(2,connexion.getPseudo());
                    prepared.execute();
                }
                rs2.close();
                return key;
            }
            rs2.close();
            return "-1";
        }catch (Exception e){
            e.printStackTrace();
            return "-2";
        }
    }


    /**
     * Méthode qui permet d'ajouter des consommations d'alcool à un compte
     * @param json de type String qui se présente sous la forme { key: "", id_boisson: 1 } afin d'identifier l'utilisateur ainsi que la consommation à ajouter
     */
    @POST
    @Path("/conso")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addconso(String json){
        Gson creator = new Gson();
        Json_conso conso = creator.fromJson(json, Json_conso.class);
        Date date = new Date();
        try {
            Connection con = Services.getCO();
            PreparedStatement prepared = con.prepareStatement("SELECT pseudo FROM Utilisateur WHERE IDUser =?");
            prepared.setString(1,conso.getKey());
            ResultSet rs = prepared.executeQuery();
            rs.next();
            String pseudo  = rs.getString("pseudo");
            rs.close();

            PreparedStatement prepared2 = con.prepareStatement("insert into Consommation values (?,?,?)");
            prepared2.setString(1,pseudo);
            prepared2.setInt(2,conso.getId());
            prepared2.setTimestamp(3,new Timestamp(date.getTime()));
            prepared2.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui permet de créer un compte
     * @param json de type String qui se présente sous la forme { poids: 1, sexe: true, publique: true, cond_a: true, pseudo: "", mdp: "" } qui représente l'utilisateur
     * @return un int représentant un état :
     * 0 si tout s'est bien passé
     * 1 si une exception est levé sur le serveur
     * 2 si les informations passées ne correspondent pas à celle demandées au minimum
     * 3 si le pseudo existe déjà
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public int create(String json) {
        Gson creator = new Gson();
        Compte compte = creator.fromJson(json, Compte.class);
        try {
            if(compte.getPseudo().length() > 2 && compte.getMdp().length() > 4 && compte.getPoids() > 0) {
                Connection con = Services.getCO();
                PreparedStatement prepared = con.prepareStatement("Select pseudo from Compte WHERE pseudo =?");
                prepared.setString(1, compte.getPseudo());
                ResultSet rs = prepared.executeQuery();
                if(!rs.next()) {
                    prepared = con.prepareStatement("insert into Compte (pseudo,pwd,sexe,poids, publique, conducteur_a) values (?,?,?,?,?,?)");
                    prepared.setString(1, compte.getPseudo());
                    prepared.setString(2, compte.getMdp());
                    prepared.setBoolean(3, compte.getSexe());
                    prepared.setDouble(4, compte.getPoids());
                    prepared.setBoolean(5, compte.getPublique());
                    prepared.setBoolean(6, compte.getA());
                    prepared.execute();
                    return 0;
                }
                return 3;
            }
            return 2;
        } catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Méthode qui permet de connaitre le taux maximum d'un compte si le demandeur en a les droits
     * @param json de type String qui se présente sous la forme { pseudo: "", key: "" }
     * @return soit :
     * un double représentant le taux maximal atteint du compte
     * -2 si une exception est levée
     * -1 si le demandeur n'a pas les droits
     */
    @POST
    @Path("/max")
    @Consumes(MediaType.APPLICATION_JSON)
    public double max(String json){
        Gson creator = new Gson();
        Json_taux compte = creator.fromJson(json, Json_taux.class);
        try {
            return getTaux(compte,1);
        } catch (Exception e) {
            return -2;
        }
    }

    /**
     * Méthode qui permet de connaitre le taux actuel d'un compte si le demandeur en a les droits
     * @param json de type String qui se présente sous la forme { pseudo: "", key: "" }
     * @return soit :
     * un double représentant le taux actuel du compte
     * -2 si une exception est levée
     * -1 si le demandeur n'a pas les droits
     */
    @POST
    @Path("/taux")
    @Consumes(MediaType.APPLICATION_JSON)
    public double taux(String json) {
        Gson creator = new Gson();
        Json_taux compte = creator.fromJson(json, Json_taux.class);
        try {
            return getTaux(compte,0);
        } catch (Exception e) {
            return -2;
        }
    }

    /**
     * Méthode qui permet d'estimer le temps restant avant d'être sous le seuil légal pour pouvoir prendre le volant
     * @param json de type String qui se présente sous la forme { pseudo: "", key: "" }
     * @return un String représentant le temps restant sous la forme "11h11" ou null si une exception est levé
     */
    @POST
    @Path("/temps")
    @Consumes(MediaType.APPLICATION_JSON)
    public String temps(String json) {
        Gson creator = new Gson();
        Json_taux compte = creator.fromJson(json, Json_taux.class);
        try {
            Connection con = Services.getCO();
            double taux = getTaux(compte,0);
            if(taux >= 0) {
                PreparedStatement prepared2 = con.prepareStatement("SELECT sexe, conducteur_a FROM Compte where pseudo=?");
                prepared2.setString(1, compte.getPseudo());
                ResultSet rs = prepared2.executeQuery();
                rs.next();
                double max,tmp, sexe;
                int tmp2, minutes, heure;
                if(rs.getBoolean(2))
                    max = 0.2;
                else
                    max = 0.5;
                if(rs.getBoolean(1))
                    sexe = 0.13;
                else
                    sexe = 0.09;

                if(taux < max)
                    return "00h00";
                tmp = (taux - max) / sexe;
                tmp2 = (int) (tmp - (int)tmp);
                minutes = (int) ((tmp - tmp2)*100*0.6);
                heure  = minutes/60;
                minutes = minutes%60;

                if(minutes < 10)
                    return heure+"h0"+minutes;
                return heure+"h"+minutes;
            }
            return "00h00";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Méthode qui supprime le compte de la BDD
     * @param json de type String qui se présente sous la forme { pseudo: "", key: "" }
     * @return un booléen valant true si tout se passe bien et false si la clef n'est pas bonne ou si une exception est levée
     */
    @POST
    @Path("/suppr")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean suppr(String json) {
        try {
            Connection con = Services.getCO();
            Gson creator = new Gson();
            Json_taux compte = creator.fromJson(json, Json_taux.class);
            PreparedStatement prepared2 = con.prepareStatement("SELECT IDUser FROM Utilisateur where pseudo=?");
            prepared2.setString(1, compte.getPseudo());
            ResultSet rs = prepared2.executeQuery();
            rs.next();
            String key = rs.getString(1);
            if (key.equals(compte.getKey())) {
                prepared2 = con.prepareStatement("DELETE FROM Consommation where pseudo=?");
                prepared2.setString(1, compte.getPseudo());
                prepared2.execute();
                prepared2 = con.prepareStatement("DELETE FROM Utilisateur where pseudo=?");
                prepared2.setString(1, compte.getPseudo());
                prepared2.execute();
                prepared2 = con.prepareStatement("DELETE FROM Compte where pseudo=?");
                prepared2.setString(1, compte.getPseudo());
                prepared2.execute();
                return true;
            }return false;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Méthode qui permet d'accéder aux informations d'un profil ( pseudo, taux maximal et taux actuel )
     * @param json de type String qui se présente sous la forme { pseudo: "", key: "" }
     * @return soit :
     * un String sous la forme { taux_actuel:taux_max }
     * "-1" si le demandeur n'a pas les droits
     * "-2" si une exception est levée
     */
    @POST
    @Path("/profil")
    public String profil(String json) {
        try {
            Gson creator = new Gson();
            Json_taux compte = creator.fromJson(json, Json_taux.class);
            Connection con = Services.getCO();
            if (!aLeDroit(compte))
                return "-1";
            return getTaux(compte, 0) + ":" + getTaux(compte, 1);
        }catch (Exception e){
            return "-2";
        }
    }

    /**
     * Méthode qui permet de chercher les pseudos qui commencent par un certains String
     * @param name de type String qui représente le début du pseudo recherché
     * @return la liste des pseudos correspondant sous la forme [pseudo1,pseudo2,]
     */
    @POST
    @Path("/search")
    public String search(String name){
        try {
            Connection con = Services.getCO();
            PreparedStatement prepared = con.prepareStatement("SELECT pseudo FROM Compte WHERE pseudo LIKE ?");
            prepared.setString(1, name + "%");
            ResultSet rs = prepared.executeQuery();
            String res = "[";
            while (rs.next()) {
                res += rs.getString(1) + ",";
            }
            res += "]";
            rs.close();
            return res;
        }catch(Exception e){
            return "-1";
        }
    }

    /**
     * Méthode qui permet de savoir si un utilisateur a les droits sur les informations du profil demandé
     * @param compte de type Json_taux
     * @return true si le demandeur a les droits. Il a les droits si :
     * - il est propriétaire du compte ( il a fournit la clef du compte avec )
     * - le profil est en public
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    static boolean aLeDroit(Json_taux compte) throws SQLException, ClassNotFoundException {
        Connection con = Services.getCO();
        if( compte.getKey().equals("") ){
            PreparedStatement prepared;
            prepared = con.prepareStatement("SELECT publique FROM Compte WHERE pseudo =?");
            prepared.setString(1,compte.getPseudo());
            ResultSet rs = prepared.executeQuery();
            rs.next();
            boolean droit = rs.getBoolean(1);
            rs.close();
            if(!droit)
                return false;
        }else{
            PreparedStatement prepared = con.prepareStatement("SELECT IDUser FROM Utilisateur WHERE pseudo =?");
            prepared.setString(1,compte.getPseudo());
            ResultSet rs = prepared.executeQuery();
            rs.next();
            String key = rs.getString(1);
            rs.close();
            if(!key.equals(compte.getKey()))
                return false;
        }
        return true;
    }


    // index 0 = actuel , index 1 = max

    /**
     * Méthode qui permet de retourner le taux d'un compte ( actuel ou maximal )
     * @param compte de type Json_taux
     * @param index valant 0 si le demandeur souhaite le taux actuel et 1 si il souhaite le taux maximal
     * @return
     */
    static double getTaux(Json_taux compte, int index) {
        try {
            Connection con = Services.getCO();
            if (aLeDroit(compte)) {

                String pseudo = compte.getPseudo();
                double max = 0.0;

                PreparedStatement prepared = con.prepareStatement("SELECT sexe, poids FROM Compte WHERE pseudo =?");
                prepared.setString(1, pseudo);
                ResultSet rs = prepared.executeQuery();
                rs.next();
                boolean sexe = rs.getBoolean(1);
                double poids = rs.getDouble(2);
                rs.close();

                prepared = con.prepareStatement("SELECT id_boisson, heure FROM Consommation WHERE pseudo =? order by heure asc");
                prepared.setString(1, pseudo);
                rs = prepared.executeQuery();
                PreparedStatement prep;
                ResultSet rs2;
                double taux = 0.0;
                double tmp;
                Timestamp time = null;
                Date d = new Date();
                int temp;
                while (rs.next()) {
                    prep = con.prepareStatement("SELECT quantite, taux FROM Boisson WHERE id_boisson =?");
                    prep.setInt(1, rs.getInt(1));
                    rs2 = prep.executeQuery();
                    rs2.next();
                    tmp = (rs2.getDouble(1) * (rs2.getDouble(2) / 100) * 0.8) / (poids * 0.7);

                    taux += tmp;
                    if (taux > max)
                        max = taux;
                    if (time != null) {
                        temp = (int) ((rs.getTimestamp(2).getTime() - time.getTime()) / 1000);
                        temp = temp / 300;
                        taux -= 0.01 * temp;
                    }
                    time = rs.getTimestamp(2);
                }
                rs.close();

                if (time != null) {
                    temp = (int) ((d.getTime() - time.getTime()) / 1000);
                    temp = temp / 300;
                    taux -= 0.01 * temp;
                }

                if (taux < 0)
                    taux = 0;

                if (index == 0)
                    return taux;
                else
                    return max;
            }
            return -1;
        }catch (Exception e){
            return -1;
        }
    }

    /**
     * Méthode qui retourne la connexion à la base de données pour éviter d'être créée à chaque appel de web service
     * @return la connexion en cours
     */
    static Connection getCO() throws ClassNotFoundException, SQLException {
        if(Services.conn == null) {
            Class.forName("com.mysql.jdbc.Driver");
            Services.conn = DriverManager.getConnection("jdbc:mysql://mysql-poivrometre.alwaysdata.net:3306/poivrometre_poivrometre", "199493_admin", "gestiondeprojet_poivrometre");
        }
        return Services.conn;
    }

    /**
     * Méthode qui retourne une chaine de caractère générée aléatoirement
     * @param n de type int qui représente la longueur de la chaine souhaitée
     * @return de type String qui représente la chaine créée
     */
    static String getAlphaNumericString(int n)
    {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length()* Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}