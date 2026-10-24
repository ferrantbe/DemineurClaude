// build_demineur.js — l'enonce du demineur, projet facultatif.
//
//     node _outils/build_demineur.js
//     python3 _outils/ajouter_pagination.py _documents/Demineur_enonce.docx "Demineur" <pages>
//     python3 _outils/typographie.py _documents/Demineur_enonce.docx
//
// L'enonce suit le corrige de src/ : les etapes D.2 a D.6 sont celles des
// marqueurs //@ecrire de Grille.java, et chaque etape dit quels tests de
// GrilleTest passent au vert. Si l'un change, l'autre doit suivre.

const fs = require("fs");
const B = require(__dirname + "/boite.js");
const {
  p, runs, image, bullet, bulletRuns, code, h1, h2, tableau, encadre,
  saut, doc, Packer, GREY, ACCENT, AlignmentType, Paragraph, TextRun
} = B;

const SCHEMAS = __dirname + "/../_documents/schemas/";
const d = [];

function etape(codeEx, titre) {
  d.push(new Paragraph({
    spacing: { before: 300, after: 130 }, keepNext: true,
    children: [
      new TextRun({ text: codeEx + "  ", bold: true, size: 28, color: "7B9E4B" }),
      new TextRun({ text: titre, bold: true, size: 28, color: ACCENT })
    ]
  }));
}

function auVert(tests) {
  const verbe = tests.length === 1 ? "Passe au vert : " : "Passent au vert : ";
  d.push(runs([{ t: verbe, b: true }, ...tests.flatMap((t, i) =>
    i === 0 ? [{ t, code: true }] : [", ", { t, code: true }]), "."]));
}

// --- couverture ------------------------------------------------------
d.push(new Paragraph({
  spacing: { before: 1100, after: 120 }, alignment: AlignmentType.CENTER,
  children: [new TextRun({ text: "Projet facultatif", size: 26, color: GREY })]
}));
d.push(new Paragraph({
  spacing: { after: 160 }, alignment: AlignmentType.CENTER,
  children: [new TextRun({ text: "Démineur", bold: true, size: 54, color: ACCENT })]
}));
d.push(new Paragraph({
  spacing: { after: 700 }, alignment: AlignmentType.CENTER,
  children: [new TextRun({ text: "Les règles du jeu dans une classe, la fenêtre fournie",
                           size: 26, color: GREY })]
}));
d.push(p("EPHEC — Bachelier en informatique, niveau 2", { align: AlignmentType.CENTER, color: GREY }));
d.push(p("Année académique 2026-2027", { align: AlignmentType.CENTER, color: GREY }));
d.push(saut());

// --- presentation ----------------------------------------------------
d.push(h1("Ce projet"));
d.push(p("Un démineur jouable, dans une fenêtre JavaFX. Vous n'en écrivez qu'une classe : Grille, qui contient toutes les règles du jeu. La fenêtre, l'application et son lanceur vous sont fournis, ainsi que onze tests JUnit."));
d.push(runs([
  { t: "C'est un travail en bonus. ", b: true },
  "Il n'y a rien à remettre et rien n'est évalué. Faites-le seul, à votre rythme, si vous voulez voir une application complète se construire autour d'un modèle que vous avez écrit et testé."
]));
d.push(runs([
  { t: "Un corrigé est publié avec cet énoncé, dans un document séparé. ", b: true },
  "Il donne une façon d'écrire chaque méthode, et répond aux questions posées en cours de route. Ne l'ouvrez qu'une fois les tests d'une étape au vert, pour comparer, ou si une étape vous résiste depuis un moment : c'est en cherchant que l'on apprend, pas en lisant la réponse."
]));
d.push(h2("Ce que vous recevez"));
d.push(tableau(["Classe", "Rôle", "À vous ?"], [
  ["Grille", "le modèle : les mines, les cases, les règles", "en partie"],
  ["GrilleTest", "onze tests des règles, sans fenêtre", "non, fournie"],
  ["VueDemineur", "la vue et le contrôleur : un clic devient un appel à Grille", "non, fournie"],
  ["Demineur", "l'application : hérite d'Application, remplit la fenêtre", "non, fournie"],
  ["LanceurDemineur", "le point d'entrée : c'est lui qu'on lance", "non, fourni"]
], [2400, 5160, 1800], [0]));
d.push(h2("La règle qui compte"));
d.push(runs([
  { t: "Grille n'importe rien de JavaFX. ", b: true },
  "C'est ce qui la rend testable en une seconde, sans ouvrir de fenêtre, et c'est la séparation entre modèle et vue : la vue demande, le modèle décide. Si vous vous surprenez à écrire « import javafx » dans Grille, arrêtez-vous : la question appartient à la vue."
]));
d.push(h2("Comment travailler"));
d.push(bullet("Ouvrez le projet dans IntelliJ comme un projet Maven : JavaFX et JUnit sont déclarés dans le pom.xml, rien à installer."));
d.push(bullet("Comme dans les fascicules, les méthodes à écrire lèvent UnsupportedOperationException ; le message dit l'étape qui les attend. Écrivez-les dans l'ordre des étapes."));
d.push(bullet("Lancez GrilleTest après chaque étape. L'énoncé dit quels tests doivent passer au vert : les autres peuvent encore échouer, c'est normal."));
d.push(bullet("Ne lancez le jeu qu'à la fin : la fenêtre appelle presque toutes les méthodes de Grille dès son ouverture."));

// --- les regles ------------------------------------------------------
d.push(h1("Les règles du jeu"));
d.push(p("Une grille de cases cachées, dont certaines cachent une mine. Découvrir une case minée fait perdre. Découvrir une case saine montre combien de mines la touchent, parmi ses huit voisines. On gagne quand toutes les cases saines sont découvertes."));
d.push(p("Une case sans mine voisine ne dit rien d'utile : le jeu découvre alors ses voisines à votre place, et ainsi de suite. C'est ce qui ouvre d'un seul clic de grandes zones vides."));
d.push(p("Le joueur peut aussi marquer une case. Le premier marquage pose un drapeau, « il y a une mine ici » ; le second un point d'interrogation, « peut-être » ; le troisième efface la marque. Seul le drapeau protège la case d'un clic malheureux : un doute est une note, pas une décision."));

d.push(...image(SCHEMAS + "capture_partie.png", 200, "Une partie en cours : les chiffres comptent les mines voisines, les astérisques sont des drapeaux."));

// --- les etapes -----------------------------------------------------
d.push(h1("Les étapes"));

etape("D.1", "Lire ce qui est fourni");
d.push(p("Avant d'écrire, lisez Grille. Une partie est déjà là, et vous vous en servirez à chaque étape."));
d.push(tableau(["Élément fourni", "Ce qu'il fait"], [
  ["enum Etat", "CACHEE, DECOUVERTE, DRAPEAU, DOUTE : l'état d'une case"],
  ["mines, etats", "deux tableaux à deux dimensions, ligne puis colonne"],
  ["Grille(lignes, colonnes, mines)", "le constructeur du jeu : des mines au hasard"],
  ["Grille(lignes, colonnes)", "le constructeur des tests : une grille vide"],
  ["poserMine", "pose une mine où l'on veut, et tient le compte des cases saines"],
  ["dansLaGrille", "dit si une position existe : vous en aurez besoin"],
  ["etatDe, estMinee, estPerdue", "lisent l'état d'une case ou de la partie"]
], [3400, 5960], [0]));
d.push(auVert(["grilleImpossible"]));
d.push(h2("Questions à se poser"));
d.push(bullet("Pourquoi deux constructeurs ? Que deviendraient les tests si les mines étaient toujours placées au hasard ?"));
d.push(bullet("Le constructeur refuse une grille de neuf mines sur neuf cases. Que se passerait-il sans ce contrôle ? Lisez placerAuHasard avant de répondre."));

etape("D.2", "Compter les mines voisines");
d.push(runs([
  "Écrivez ", { t: "minesVoisines(ligne, colonne)", code: true },
  " : le nombre de mines parmi les huit cases qui entourent celle-ci. Une case au bord ou dans un coin a moins de huit voisines, et une mine ne se compte pas elle-même."
]));
d.push(runs([
  { t: "Indice. ", b: true },
  "Deux boucles imbriquées, de -1 à 1, donnent les neuf décalages autour de la case. Il en reste huit une fois qu'on a écarté le décalage nul, et ", { t: "dansLaGrille", code: true },
  " écarte ceux qui sortent de la grille."
]));
d.push(...image(SCHEMAS + "voisines.png", 400));
d.push(auVert(["minesVoisines"]));

etape("D.3", "Découvrir une case");
d.push(runs([
  "Écrivez ", { t: "decouvrir(ligne, colonne)", code: true },
  ", sans la propagation pour l'instant. Dans l'ordre :"
]));
d.push(bullet("une position hors de la grille, ou une partie déjà perdue : on ne fait rien ;"));
d.push(bullet("une case déjà découverte, ou marquée d'un drapeau : on ne fait rien ; une case en doute, elle, se découvre ;"));
d.push(bullet("sinon la case devient DECOUVERTE, et si elle est minée, la partie est perdue."));
d.push(auVert(["propagationLimitee", "plusRienApresLaFin"]));

etape("D.4", "La propagation");
d.push(runs([
  "Complétez ", { t: "decouvrir", code: true },
  " : si la case découverte est saine et n'a aucune mine voisine, découvrez ses huit voisines. Une voisine sans mine voisine découvrira les siennes, et ainsi de suite."
]));
d.push(runs([
  { t: "C'est une méthode qui s'appelle elle-même. ", b: true },
  "Elle tient en quelques lignes : la méthode appelle ", { t: "decouvrir", code: true },
  " sur chaque voisine. Ce que vous avez écrit à l'étape D.3 suffit à arrêter la récursion."
]));
d.push(...image(SCHEMAS + "propagation.png", 440));
d.push(auVert(["propagation"]));
d.push(h2("Questions à se poser"));
d.push(bullet("Pourquoi la récursion s'arrête-t-elle ? Quelle ligne de l'étape D.3 l'empêche de tourner en rond entre deux cases vides voisines ?"));
d.push(bullet("La propagation découvre-t-elle une case marquée d'un drapeau ? Une case en doute ? Est-ce le comportement que vous attendiez en jouant ?"));

etape("D.5", "Marquer une case");
d.push(runs([
  "Écrivez ", { t: "basculerDrapeau(ligne, colonne)", code: true },
  " : chaque appel fait passer la case à la marque suivante. Une case découverte ne change plus."
]));
d.push(...code(["CACHEE  ->  DRAPEAU  ->  DOUTE  ->  CACHEE"]));
d.push(runs([
  "Un ", { t: "switch", code: true },
  " sur l'état de la case convient bien : chaque état dit vers quoi il passe. C'est Grille qui décide de cet enchaînement ; la vue se contente de demander la marque suivante et d'afficher ce qu'on lui rend."
]));
d.push(auVert(["marquageEnTroisTemps", "seulLeDrapeauProtege", "pasDeMarqueSurUneCaseDecouverte"]));

etape("D.6", "Gagner, perdre, terminer");
d.push(runs([
  "Écrivez ", { t: "estGagnee()", code: true },
  " : la partie est gagnée quand toutes les cases saines sont découvertes, et jamais si elle est perdue. Puis ",
  { t: "estTerminee()", code: true }, " : perdue ou gagnée."
]));
d.push(runs([
  { t: "Indice. ", b: true },
  "Le champ ", { t: "casesSaines", code: true },
  " est tenu à jour par ", { t: "poserMine", code: true },
  " : il dit combien de cases il faut découvrir pour gagner. Il ne reste qu'à compter celles qui le sont."
]));
d.push(auVert(["grilleNeuve", "clicSurMine", "victoire"]));
d.push(p("Les onze tests doivent maintenant passer."));

etape("D.7", "Jouer");
d.push(runs([
  "Lancez ", { t: "LanceurDemineur", code: true }, ", et non ", { t: "Demineur", code: true },
  ". Lancée directement, ", { t: "Demineur", code: true },
  " refuse de démarrer : elle hérite d'une classe de JavaFX, et le lanceur de Java exige alors que JavaFX soit chargé d'une manière que Maven ne prévoit pas. ",
  { t: "LanceurDemineur", code: true },
  " n'hérite de rien et se contente de démarrer ", { t: "Demineur", code: true },
  " : le contrôle ne se déclenche pas. Son commentaire en dit plus, et le mémento technique y revient en TEC-FX.4."
]));
d.push(runs([
  "Si le lancement résiste malgré tout, passez par la fenêtre Maven d'IntelliJ : ",
  { t: "Plugins", code: true }, ", puis ", { t: "javafx", code: true }, ", puis ", { t: "javafx:run", code: true }, "."
]));
d.push(bullet("un clic découvre une case ; un clic droit, Maj+clic ou Ctrl+clic la marque ;"));
d.push(bullet("un clic sur une zone vide en ouvre toute l'étendue d'un coup ;"));
d.push(bullet("un drapeau protège sa case, un point d'interrogation non ;"));
d.push(bullet("la partie annonce la victoire ou la défaite, et ne réagit plus ensuite."));
d.push(runs([
  "Remarquez que vous n'avez écrit aucune ligne de la fenêtre, et que les règles étaient justes ",
  { t: "avant", i: true }, " de l'ouvrir : les tests l'avaient prouvé."
]));

// --- pour aller plus loin -------------------------------------------
d.push(h1("Pour aller plus loin"));
d.push(runs([
  { t: "Le premier clic. ", b: true },
  "Dans la plupart des démineurs, le premier clic ne tombe jamais sur une mine. Ici, il peut. Que faudrait-il changer pour l'empêcher ? Dans Grille, dans la vue, ou dans les deux ? Pourquoi le constructeur actuel ne le permet-il pas ?"
]));
d.push(runs([
  { t: "Les drapeaux restants. ", b: true },
  "Le jeu d'origine affiche le nombre de mines moins le nombre de drapeaux posés. Quelle méthode faudrait-il ajouter à Grille, et que devrait faire la vue de sa réponse ?"
]));
d.push(runs([
  { t: "Une grille plus grande. ", b: true },
  "Essayez une grille de cent lignes sur cent colonnes, presque sans mines. Que devient la récursion de la propagation ? Comment pourrait-on s'en passer ?"
]));

Packer.toBuffer(doc(d, "Démineur — énoncé")).then(b => {
  fs.writeFileSync(__dirname + "/../_documents/Demineur_enonce.docx", b);
  console.log("Demineur :", Math.round(b.length / 1024), "Ko");
});
