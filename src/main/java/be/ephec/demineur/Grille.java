package be.ephec.demineur;

import java.util.Random;

/**
 * Le MODELE du demineur. CORRIGE.
 *
 * Les methodes precedees d'un marqueur //@ecrire sont celles que
 * l'etudiant ecrit : _outils/depot_demineur.py remplace leur corps par une
 * exception dans le projet qu'il recoit. Tout le reste lui est fourni.
 *
 * Aucun import JavaFX. C'est la regle de l'exercice, et c'est ce qui rend
 * cette classe entierement testable en JUnit : placer des mines a des
 * positions connues, decouvrir une case, verifier le compte et la
 * propagation.
 *
 * Elle fonctionnerait telle quelle avec une interface en console. Si ce
 * n'etait pas le cas, elle en saurait trop sur la vue.
 */
public class Grille {

    /**
     * Les quatre etats d'une case.
     *
     * DRAPEAU et DOUTE sont deux marques distinctes, et la distinction
     * vient du jeu lui-meme : on pose un drapeau la ou l'on est SUR qu'il
     * y a une mine, un point d'interrogation la ou l'on hesite.
     *
     * Seul le DRAPEAU protege contre un clic : une case en DOUTE se
     * decouvre normalement. C'est la regle du demineur d'origine, et elle
     * a du sens — un doute n'est pas une decision.
     */
    public enum Etat { CACHEE, DECOUVERTE, DRAPEAU, DOUTE }

    private final int lignes;
    private final int colonnes;
    private final boolean[][] mines;
    private final Etat[][] etats;

    private boolean perdue = false;
    private int casesSaines;

    /**
     * Constructeur de jeu : les mines sont placees au hasard.
     *
     * Il reste au moins une case saine, sans quoi la partie serait perdue
     * avant le premier clic. Le controle n'est pas une precaution de
     * principe : sans lui, placerAuHasard chercherait indefiniment une
     * case libre qui n'existe pas, et le programme se figerait sans un
     * message. Voir CC-ERR.1.
     */
    public Grille(int lignes, int colonnes, int nombreDeMines) {
        this(lignes, colonnes);
        if (nombreDeMines < 0 || nombreDeMines >= lignes * colonnes) {
            throw new IllegalArgumentException("Entre 0 et " + (lignes * colonnes - 1)
                    + " mines sur une grille de " + lignes + " x " + colonnes
                    + ", pas " + nombreDeMines);
        }
        placerAuHasard(nombreDeMines, new Random());
    }

    /**
     * Constructeur de TEST : la grille est vide, et l'appelant place les
     * mines ou il veut par poserMine. Sans lui, aucun test ne serait
     * reproductible — c'est la meme raison qui fait passer un Random en
     * parametre ailleurs dans le cours.
     */
    public Grille(int lignes, int colonnes) {
        if (lignes < 1 || colonnes < 1) {
            throw new IllegalArgumentException(
                    "Une grille compte au moins une ligne et une colonne, pas "
                    + lignes + " x " + colonnes);
        }
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.mines = new boolean[lignes][colonnes];
        this.etats = new Etat[lignes][colonnes];
        this.casesSaines = lignes * colonnes;

        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                etats[l][c] = Etat.CACHEE;
            }
        }
    }

    public void poserMine(int ligne, int colonne) {
        if (!mines[ligne][colonne]) {
            mines[ligne][colonne] = true;
            casesSaines--;
        }
    }

    private void placerAuHasard(int nombre, Random hasard) {
        int posees = 0;
        while (posees < nombre) {
            int ligne = hasard.nextInt(lignes);
            int colonne = hasard.nextInt(colonnes);
            if (!mines[ligne][colonne]) {
                poserMine(ligne, colonne);
                posees++;
            }
        }
    }

    public int getLignes() {
        return lignes;
    }

    public int getColonnes() {
        return colonnes;
    }

    public Etat etatDe(int ligne, int colonne) {
        return etats[ligne][colonne];
    }

    public boolean estMinee(int ligne, int colonne) {
        return mines[ligne][colonne];
    }

    /** Le nombre de mines dans les huit cases voisines. */
    //@ecrire D.2
    public int minesVoisines(int ligne, int colonne) {
        int compte = 0;
        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) {
                    continue;
                }
                int l = ligne + dl;
                int c = colonne + dc;
                if (dansLaGrille(l, c) && mines[l][c]) {
                    compte++;
                }
            }
        }
        return compte;
    }

    private boolean dansLaGrille(int ligne, int colonne) {
        return ligne >= 0 && ligne < lignes && colonne >= 0 && colonne < colonnes;
    }

    /**
     * Decouvre une case. Si elle n'a aucune mine voisine, la decouverte
     * se propage aux huit voisines — c'est l'algorithme qui donne au jeu
     * son caractere, et le seul qui merite d'etre teste serieusement.
     *
     * La recursion s'arrete d'elle-meme : une case deja decouverte est
     * ignoree, et le nombre de cases est fini.
     */
    //@ecrire D.3 et D.4
    public void decouvrir(int ligne, int colonne) {
        if (!dansLaGrille(ligne, colonne) || perdue) {
            return;
        }
        // Le DOUTE ne protege pas : c'est une note qu'on se laisse, pas
        // une decision. Le DRAPEAU, lui, bloque — c'est son interet, et
        // c'est la regle du demineur d'origine.
        if (etats[ligne][colonne] != Etat.CACHEE && etats[ligne][colonne] != Etat.DOUTE) {
            return;
        }

        etats[ligne][colonne] = Etat.DECOUVERTE;

        if (mines[ligne][colonne]) {
            perdue = true;
            return;
        }

        if (minesVoisines(ligne, colonne) == 0) {
            for (int dl = -1; dl <= 1; dl++) {
                for (int dc = -1; dc <= 1; dc++) {
                    decouvrir(ligne + dl, colonne + dc);
                }
            }
        }
    }

    /**
     * Fait passer une case a la marque suivante.
     *
     *     CACHEE  ->  DRAPEAU  ->  DOUTE  ->  CACHEE
     *
     * Trois etats plutot que deux, parce que deux ne suffisent pas a
     * jouer : le drapeau dit « il y a une mine ici », le doute dit
     * « peut-etre ». Sans le second, on marque d'un drapeau des cases
     * dont on n'est pas sur, et on finit par ne plus savoir lesquelles.
     *
     * Une case decouverte ne bouge plus : il n'y a plus rien a marquer.
     */
    //@ecrire D.5
    public void basculerDrapeau(int ligne, int colonne) {
        switch (etats[ligne][colonne]) {
            case CACHEE -> etats[ligne][colonne] = Etat.DRAPEAU;
            case DRAPEAU -> etats[ligne][colonne] = Etat.DOUTE;
            case DOUTE -> etats[ligne][colonne] = Etat.CACHEE;
            case DECOUVERTE -> { }
        }
    }

    public boolean estPerdue() {
        return perdue;
    }

    /** Gagnee quand toutes les cases sans mine sont decouvertes. */
    //@ecrire D.6
    public boolean estGagnee() {
        if (perdue) {
            return false;
        }
        int decouvertes = 0;
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                if (etats[l][c] == Etat.DECOUVERTE && !mines[l][c]) {
                    decouvertes++;
                }
            }
        }
        return decouvertes == casesSaines;
    }

    //@ecrire D.6
    public boolean estTerminee() {
        return perdue || estGagnee();
    }
}
