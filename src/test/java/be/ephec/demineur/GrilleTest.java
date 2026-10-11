package be.ephec.demineur;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FOURNIE : la preuve que les regles du jeu se testent sans fenetre.
 *
 * Aucun de ces tests n'ouvre de fenetre. C'est tout l'interet d'avoir
 * separe Grille de VueDemineur : les regles du jeu se verifient en une
 * seconde, et on sait qu'elles sont justes AVANT d'avoir affiche quoi que
 * ce soit.
 *
 * Le constructeur a deux arguments existe pour ces tests : il cree une
 * grille vide, dont on place les mines soi-meme. Avec un placement au
 * hasard, aucun de ces tests ne serait reproductible.
 */
class GrilleTest {

    @Test
    @DisplayName("Une grille neuve est entierement cachee")
    void grilleNeuve() {
        Grille grille = new Grille(3, 3);

        assertEquals(Grille.Etat.CACHEE, grille.etatDe(0, 0));
        assertFalse(grille.estPerdue());
        assertFalse(grille.estGagnee());
    }

    @Test
    @DisplayName("Le compte des mines voisines tient compte des bords")
    void minesVoisines() {
        Grille grille = new Grille(3, 3);
        grille.poserMine(0, 0);

        assertEquals(1, grille.minesVoisines(1, 1), "la diagonale compte");
        assertEquals(1, grille.minesVoisines(0, 1));
        assertEquals(0, grille.minesVoisines(2, 2), "trop loin");
        assertEquals(0, grille.minesVoisines(0, 0), "une mine ne se compte pas elle-meme");
    }

    @Test
    @DisplayName("Decouvrir une case vide propage aux voisines")
    void propagation() {
        Grille grille = new Grille(3, 3);
        grille.poserMine(0, 0);

        grille.decouvrir(2, 2);

        assertEquals(Grille.Etat.DECOUVERTE, grille.etatDe(1, 1),
                "la propagation traverse les cases sans mine voisine");
        assertEquals(Grille.Etat.CACHEE, grille.etatDe(0, 0),
                "elle ne decouvre jamais la mine elle-meme");
    }

    @Test
    @DisplayName("La propagation s'arrete aux cases qui touchent une mine")
    void propagationLimitee() {
        Grille grille = new Grille(1, 3);
        grille.poserMine(0, 1);

        grille.decouvrir(0, 0);

        assertEquals(Grille.Etat.DECOUVERTE, grille.etatDe(0, 0));
        assertEquals(Grille.Etat.CACHEE, grille.etatDe(0, 2),
                "(0,0) touche une mine : la decouverte ne va pas plus loin");
    }

    @Test
    @DisplayName("Cliquer sur une mine perd la partie")
    void clicSurMine() {
        Grille grille = new Grille(2, 2);
        grille.poserMine(0, 0);

        grille.decouvrir(0, 0);

        assertTrue(grille.estPerdue());
        assertFalse(grille.estGagnee(), "une partie perdue n'est pas gagnee");
        assertTrue(grille.estTerminee());
    }

    @Test
    @DisplayName("La partie est gagnee quand toutes les cases saines sont ouvertes")
    void victoire() {
        Grille grille = new Grille(1, 3);
        grille.poserMine(0, 1);

        grille.decouvrir(0, 0);
        assertFalse(grille.estGagnee(), "il reste une case saine");

        grille.decouvrir(0, 2);
        assertTrue(grille.estGagnee());
    }

    @Test
    @DisplayName("Le marquage tourne : cachee, drapeau, doute, cachee")
    void marquageEnTroisTemps() {
        Grille grille = new Grille(2, 2);

        assertEquals(Grille.Etat.CACHEE, grille.etatDe(0, 0));

        grille.basculerDrapeau(0, 0);
        assertEquals(Grille.Etat.DRAPEAU, grille.etatDe(0, 0));

        grille.basculerDrapeau(0, 0);
        assertEquals(Grille.Etat.DOUTE, grille.etatDe(0, 0));

        grille.basculerDrapeau(0, 0);
        assertEquals(Grille.Etat.CACHEE, grille.etatDe(0, 0),
                "le troisieme geste ramene au point de depart");
    }

    @Test
    @DisplayName("Le drapeau protege la case, le doute ne la protege pas")
    void seulLeDrapeauProtege() {
        Grille grille = new Grille(2, 2);

        grille.basculerDrapeau(0, 0);
        grille.decouvrir(0, 0);
        assertEquals(Grille.Etat.DRAPEAU, grille.etatDe(0, 0),
                "une case marquee d'un drapeau ne se decouvre pas par megarde");

        grille.basculerDrapeau(0, 0);
        assertEquals(Grille.Etat.DOUTE, grille.etatDe(0, 0));
        grille.decouvrir(0, 0);
        assertEquals(Grille.Etat.DECOUVERTE, grille.etatDe(0, 0),
                "un doute est une note, pas une decision : il ne bloque rien");
    }

    @Test
    @DisplayName("Une case decouverte ne se marque plus")
    void pasDeMarqueSurUneCaseDecouverte() {
        Grille grille = new Grille(2, 2);

        grille.decouvrir(0, 0);
        grille.basculerDrapeau(0, 0);

        assertEquals(Grille.Etat.DECOUVERTE, grille.etatDe(0, 0));
    }

    @Test
    @DisplayName("Une fois la partie perdue, plus rien ne se decouvre")
    void plusRienApresLaFin() {
        Grille grille = new Grille(2, 2);
        grille.poserMine(0, 0);
        grille.decouvrir(0, 0);

        grille.decouvrir(1, 1);

        assertEquals(Grille.Etat.CACHEE, grille.etatDe(1, 1));
    }

    @Test
    @DisplayName("Une grille impossible est refusee des sa construction")
    void grilleImpossible() {
        assertThrows(IllegalArgumentException.class, () -> new Grille(0, 3),
                "une grille sans ligne n'existe pas");
        assertThrows(IllegalArgumentException.class, () -> new Grille(3, 3, 9),
                "neuf mines sur neuf cases : aucune case saine");
        assertThrows(IllegalArgumentException.class, () -> new Grille(3, 3, -1));
        assertDoesNotThrow(() -> new Grille(3, 3, 8), "huit mines laissent une case saine");
    }
}
