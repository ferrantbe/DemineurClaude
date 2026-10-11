package be.ephec.demineur;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Le point d'entree du demineur.
 *
 * POURQUOI CE PROJET EST SEPARE DU FIL ROUGE. Le demineur y figurait
 * d'abord comme un jeu du hub, au meme titre que le Nim ou la bataille.
 * Ca ne tenait pas : le hub est une application CONSOLE, et ouvrir une
 * fenetre depuis une console demande de demarrer le moteur graphique a la
 * main, de construire l'interface sur un autre fil d'execution, et de
 * faire attendre le premier. Trois notions qui ne sont ni au programme ni
 * a l'examen, pour un seul jeu.
 *
 * Ici, rien de tout cela. La classe herite d'Application, la methode
 * launch() demarre le moteur, et start() recoit une fenetre toute prete.
 * C'est la forme que prend TOUTE application JavaFX — celle des exercices
 * FX, et celle de la question 5 de l'examen.
 *
 * CE QU'IL FAUT EN RETENIR. Quand le code devient compliqué pour tenir
 * dans une structure, c'est souvent la structure qu'il faut changer.
 *
 * POUR LANCER LE PROGRAMME, executez LanceurDemineur, pas cette classe.
 * La raison est expliquee la-bas, et elle n'a rien d'evident.
 */
public class Demineur extends Application {

    private static final int LIGNES = 9;
    private static final int COLONNES = 9;
    private static final int MINES = 10;

    /**
     * Appelee par JavaFX une fois le moteur graphique demarre.
     *
     * La fenetre est fournie : on n'en cree pas. C'est la difference avec
     * la version qui vivait dans le hub, ou il fallait en fabriquer une.
     */
    @Override
    public void start(Stage fenetre) {
        Grille grille = new Grille(LIGNES, COLONNES, MINES);
        new VueDemineur(grille).afficherDans(fenetre);
    }

    // Pas de main ici : il est dans LanceurDemineur, et la raison y est
    // expliquee. Un main dans cette classe fonctionnerait sur certaines
    // machines et echouerait sur d'autres, ce qui est pire que pas de main
    // du tout.
}
