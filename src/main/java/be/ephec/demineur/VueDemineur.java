package be.ephec.demineur;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * La VUE et le CONTROLEUR du demineur.
 *
 * Cette classe ne connait des regles que ce que Grille lui repond. Elle
 * ne compte aucune mine, ne decide d'aucune victoire : elle traduit un
 * clic en appel au modele, puis redessine.
 *
 * C'est le meme partage qu'en FX.8, et pour la meme raison : le modele
 * reste testable, la vue reste jetable.
 */
public class VueDemineur {

    private final Grille grille;
    private final Button[][] cases;
    private final Label message = new Label();

    public VueDemineur(Grille grille) {
        this.grille = grille;
        this.cases = new Button[grille.getLignes()][grille.getColonnes()];
    }

    /**
     * Construit la scene et la pose dans la fenetre fournie.
     *
     * La fenetre vient de l'exterieur : c'est JavaFX qui la donne au
     * demarrage, et la vue n'a pas a en fabriquer une. Voir Demineur.
     */
    public void afficherDans(Stage fenetre) {
        GridPane damier = new GridPane();
        damier.setHgap(2);
        damier.setVgap(2);

        for (int ligne = 0; ligne < grille.getLignes(); ligne++) {
            for (int colonne = 0; colonne < grille.getColonnes(); colonne++) {
                Button bouton = creerCase(ligne, colonne);
                cases[ligne][colonne] = bouton;
                damier.add(bouton, colonne, ligne);
            }
        }

        VBox racine = new VBox(12, damier, message);
        racine.setAlignment(Pos.CENTER);
        racine.setStyle("-fx-padding: 15;");

        redessiner();

        fenetre.setTitle("Demineur");
        fenetre.setScene(new Scene(racine));
        fenetre.show();
    }

    private Button creerCase(int ligne, int colonne) {
        Button bouton = new Button();
        bouton.setPrefSize(36, 36);

        // Un seul gestionnaire pour tous les clics : l'evenement dit
        // lequel a ete utilise, et avec quelles touches enfoncees.
        bouton.setOnMouseClicked(evenement -> {
            if (grille.estTerminee()) {
                return;
            }
            if (poseUnDrapeau(evenement)) {
                grille.basculerDrapeau(ligne, colonne);
            } else {
                grille.decouvrir(ligne, colonne);
            }
            redessiner();
        });
        return bouton;
    }

    /**
     * Redessine TOUTE la grille a partir du modele, plutot que de
     * modifier la seule case cliquee.
     *
     * C'est volontaire : la propagation peut ouvrir vingt cases d'un
     * coup, et la vue n'a pas a savoir lesquelles. Elle demande l'etat et
     * l'affiche. Moins malin, et beaucoup plus sur.
     */
    private void redessiner() {
        for (int ligne = 0; ligne < grille.getLignes(); ligne++) {
            for (int colonne = 0; colonne < grille.getColonnes(); colonne++) {
                habiller(cases[ligne][colonne], ligne, colonne);
            }
        }
        if (grille.estPerdue()) {
            message.setText("Perdu. Fermez la fenetre pour quitter l'application.");
        } else if (grille.estGagnee()) {
            message.setText("Gagne ! Fermez la fenetre pour quitter l'application.");
        } else {
            // Sur deux lignes : la grille fait 9 cases de 36 pixels, et une
            // ligne unique elargirait la fenetre plus que le damier.
            message.setText("Clic pour decouvrir."
                    + "\nMarquer : clic droit, Maj+clic ou Ctrl+clic  ( * puis ? )");
        }
    }

    /**
     * Ce clic pose-t-il un drapeau ?
     *
     * TROIS GESTES PLUTOT QU'UN, et ce n'est pas du luxe. Le clic droit
     * est la manière naturelle, mais il n'est pas toujours disponible :
     * sur le pave tactile d'un portable Apple, il demande un reglage ou
     * un appui a deux doigts que tout le monde n'a pas active.
     *
     * On accepte donc aussi Maj+clic et Ctrl+clic. Le premier ne se
     * heurte a rien ; le second est parfois traduit par le systeme en
     * clic droit, auquel cas c'est la premiere condition qui repond — les
     * deux chemins menent au meme endroit, sans se gener.
     *
     * LA LECON, qui depasse ce jeu : une interface qui n'offre qu'une
     * seule facon de faire quelque chose exclut ceux dont le materiel ne
     * la permet pas. En ajouter une seconde coute trois lignes.
     *
     * Le geste fait tourner la marque : rien, puis l'etoile du drapeau,
     * puis le point d'interrogation du doute, puis rien a nouveau. C'est
     * la Grille qui decide de cet enchainement — la vue se contente de
     * demander le suivant et d'afficher ce qu'on lui rend.
     */
    private boolean poseUnDrapeau(MouseEvent evenement) {
        return evenement.getButton() == MouseButton.SECONDARY
                || evenement.isShiftDown()
                || evenement.isControlDown();
    }

    private void habiller(Button bouton, int ligne, int colonne) {
        switch (grille.etatDe(ligne, colonne)) {
            case CACHEE -> {
                bouton.setText("");
                bouton.setStyle("");
            }
            case DRAPEAU -> {
                bouton.setText("*");
                bouton.setStyle("-fx-font-weight: bold; -fx-text-fill: #b03030;");
            }
            case DOUTE -> {
                bouton.setText("?");
                bouton.setStyle("-fx-font-weight: bold; -fx-text-fill: #808080;");
            }
            case DECOUVERTE -> {
                if (grille.estMinee(ligne, colonne)) {
                    bouton.setText("X");
                    bouton.setStyle("-fx-background-color: #c86464;");
                } else {
                    int voisines = grille.minesVoisines(ligne, colonne);
                    bouton.setText(voisines == 0 ? "" : String.valueOf(voisines));
                    bouton.setStyle("-fx-background-color: #e8e8e8;");
                }
            }
        }
    }
}
