import com.google.gson.internal.$Gson$Types;

public class Compte {

    private double poids;
    private boolean sexe, publique, cond_a;
    private String mdp, pseudo;

    public Compte(double poids, boolean sexe, String mdp, String pseudo, boolean publique, boolean cond_a){
        this.poids = poids;
        this.sexe = sexe;
        this.mdp = mdp;
        this.pseudo = pseudo;
        this.publique = publique;
        this.cond_a = cond_a;
    }

    public double getPoids(){
        return this.poids;
    }

    public boolean getSexe(){ return this.sexe; }

    public boolean getPublique(){
        return this.publique;
    }

    public boolean getA(){
        return this.cond_a;
    }

    public String getMdp(){
        return this.mdp;
    }

    public String getPseudo(){
        return this.pseudo;
    }

}
