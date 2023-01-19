package com.example.filmaficionado_eksamen_projekt;

import com.example.filmaficionado_eksamen_projekt.dao.CategoryDAO;
import com.example.filmaficionado_eksamen_projekt.dao.CategoryMovieDAO;
import com.example.filmaficionado_eksamen_projekt.dao.MovieDAO;
import com.example.filmaficionado_eksamen_projekt.model.*;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HelloController {

    @FXML
    private BorderPane bigPane;
    @FXML
    private ListView categoryListView = new ListView();

    @FXML
    private ListView movieCategoryListView = new ListView();

    @FXML
    private ListView movieListView = new ListView();

    @FXML
    private ListView favoriteMovieInCategoryListView = new ListView();

    @FXML
    private Label selectedCategoryLabel;

    @FXML
    private Label bestMovieLabel;

    @FXML
    private Label filterMode;

    @FXML
    private TextField søgFelt;

    private int searchCounter = 0; // En integer vi bruger for at tjekke om søgning er sat til eller ej

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Opsætning af det format vi bruger når vi skal have tiden
                                                                                // fra brugerens computer.
    LocalDateTime now = LocalDateTime.now(); // Får tiden fra brugerens computer lige nu

    private static MediaPlayer moviePlayer;
    private MediaView mediaView;

    private Stage stage;
    private Scene scene;
    private BorderPane mediaBorder;


    public void initialize() {
        showMovies();
        showCategories();
        startUpMovies();
        notificationText();
    } // Initialiserer alt det vi skal bruge fra starten.

    public void startUpMovies(){
        List<Movie> startUpMovies = mdi.getLowRatedOrOldMovies(); // Benytter en metode fra MovieDaoImpl der får alle sange der er +2 år gamle eller har en dårlig personlig rating.
        selectedCategoryLabel.setText("Consider removing these"); // Sætter Labelens tekst
        for (Movie mov : startUpMovies) {
            movieCategoryListView.getItems().add(mov); // Indsætter alle de gamle / dårligt ratede film ind i påres vores store movieCategoryListView.
        }
    } // Viser film der er enten over 2 år gamle ELLER som har en dårlig personlig rating

    public void showMovies(){
        List<Movie> movies = mdi.getAllMovies();  // Benytter en metode fra MovieDaoImpl der får alle film der er oprettet
        for (Movie mov : movies)
        {
            movieListView.getItems().add(mov); // Indsætter alle film ind i movieListViewet
        }
    } // Viser alle film der findes

    public void showCategories(){
        List<Category> categories = cdi.getAllCategories(); // Benytter metode fra CategoryDaoImpl der får alle kategorier der er oprettet
        for (Category cat : categories)
        {
            categoryListView.getItems().add(cat); // Indsætter alle vores kategorier ind i vores categoryListView
        }
    } // Viser alle kategorier der findes

    public void showMoviesInCategory(){
        Category cat = (Category) categoryListView.getItems().get(0);
        List<Movie> moviesInCategory = cmdi.getMoviesInCategory(cat); // Metode fra vores CategoryMovieDaoImpl, der finder alle film der er associeret med den valgte kategori.
        selectedCategoryLabel.setText(cat.getCategoryName()); // Sætter vores label til at være navnet på den kategori vi har valgt
        for (Movie mov : moviesInCategory){
            movieCategoryListView.getItems().add(mov); // Indsætter film associeret med kategorien ind i movieCategoryListView
        }
    } // Viser film i den valgte kategori

    public void showMovieFavorites(){
        Category cat = (Category) categoryListView.getItems().get(0);
        List<Movie> movieFavorites = cmdi.getMovieFavorites(cat); // Benytter funktion fra vores CategoryMovieDaoImpl der får alle de film vi har markeret med favorit/bedste film
        for (Movie mov : movieFavorites){
            favoriteMovieInCategoryListView.getItems().add(mov); // Indsætter de film der er markeret som "bedste" ind i et listView nedenunder kategorien.
        }
    } // Viser den bedste film i den valgte kategori

    public void categoryClickTrack(javafx.scene.input.MouseEvent mouseEvent) {

        ObservableList valgteIndeks = categoryListView.getSelectionModel().getSelectedIndices(); // Laver en observable list og kalder den valgteIndeks. Gør at vi kan følge med i hvad der sker.

        for (Object indeks : valgteIndeks)
        {
            movieCategoryListView.getItems().clear();  // Clearer movieCategoryListViewet
            Category cat = (Category) categoryListView.getItems().get((int) indeks);  // Cat = den kategori der er valgt i vores categoryListView.
            selectedCategoryLabel.setText(cat.getCategoryName()); // Sætter labelens navn til at være det kategorien hedder.
            List<Movie> movies = cmdi.getMoviesInCategory(cat);  // Får alle de film der er tilknyttet "cat", det vil sige, den valgte kategori.
            List<Movie> movieFavorites = cmdi.getMovieFavorites(cat);  // cat = den valgte kategori. getMovieFavorites får altså favorit filmen fra den valgte kategori.

            movieCategoryListView.getItems().clear();
            favoriteMovieInCategoryListView.getItems().clear(); // Clearer favorit film / bedste film i kategori listen
            bestMovieLabel.setText("Best movie in" + " " + cat.getCategoryName()); // Sætter teksten foran listen

            for (Movie mov : movies){ // For each Movie i movies (getMoviesInCategory(cat) - bliver der sat ind intil der ikke er flere
                movieCategoryListView.getItems().add(mov); // Tilføjer filmene der er tilknyttet den valgte kategori til movieCategoryListViewet.
            }
            for (Movie mo : movieFavorites){ // For each Movie i movieFavorites (getMovieFavorites(cat) - bliver der sat ind intil der ikke er flere
                favoriteMovieInCategoryListView.getItems().add(mo); // Tilføjer filmen(e) der er favorit/de(n) bedste i den valgte kategori til favoriteMovieInCategoryListView.
            }
        }
    } // Tracker Category listView klik's. Det vil sige, den følger med i hvilken kategori man trykker på.

    public void movieClickTrack(MouseEvent mouseEvent) {
        ObservableList valgteIndeks = movieListView.getSelectionModel().getSelectedIndices(); // Laver en observable list og kalder den valgteIndeks. Gør at vi kan følge med i hvad der sker.
    } // Tracker Movie listView klik's. Det vil sige, den følger med i film man trykker på.

    public void movieCategoryTrack(MouseEvent mouseEvent) {

        ObservableList valgteIndeks = movieCategoryListView.getSelectionModel().getSelectedIndices(); // Laver en observable list og kalder den valgteIndeks. Gør at vi kan følge med i hvad der sker.

    } // Tracker MovieCategory listView klik's. Det vil sige, den følger med i den film man trykker på i den kategori der er blevet valgt.

    public String getFileFromSelectedMovie(){

        ObservableList valgteIndeks = movieCategoryListView.getSelectionModel().getSelectedIndices(); // Laver en observable list og kalder den valgteIndeks. Gør at vi kan følge med i hvad der sker.
        if (valgteIndeks.size() == 0) // Hvis der ikke er valgt nogen fra movieCategoryListView
            System.out.println("Nothing"); // Printer intet
        else
            for (Object indeks : valgteIndeks)  // Hvis der er valgt noget fra movieCategoryListView, får vi det indeks der er valgt på valgteIndeks (movieCategoryListView)
            {
                Movie m = (Movie) movieCategoryListView.getItems().get((int) indeks); // Vi får så en Movie m, fra det valgte indeks.
                return m.getFileName(); // Returner den valgte films fil navn
            }
        return null;
    } // Får filmen filmen der er blevet valgt i vores movieCategoryListView, og returnerer den valgte films fil navn(gør så vi kan afspille en film i vores MediaPlayer)

    public void playTrailer(MouseEvent mouseEvent) {

        ObservableList valgteIndeks = movieCategoryListView.getSelectionModel().getSelectedIndices(); // Laver en observable list og kalder den valgteIndeks. Gør at vi kan følge med i hvad der sker.

        if (valgteIndeks.size() == 0) { // Hvis vi ikke har valgt noget på vores movieCategoryListView sker følgende..

            // Vi åbner så et dialog vindue

            Dialog<ButtonType> selectAMovieToPlay = new Dialog<>(); // Ny dialog vindue
            selectAMovieToPlay.setTitle("Select a movie");
            selectAMovieToPlay.getDialogPane().getButtonTypes().addAll(ButtonType.OK); // Indsætter en 'ok' knap
            Label selectCategoryLabel = new Label("Please select a movie to add to play ");
            selectAMovieToPlay.getDialogPane().setContent(selectCategoryLabel); // Sætter vores label ind i vores dialog vindue
            Optional<ButtonType> knap1 = selectAMovieToPlay.showAndWait(); // Sætter den til at komme frem. (ShowAndWait = brugeren bliver NØDT til at trykke på vores 'ok' knap for at få den væk.
        }

        else
            for(Object indeks : valgteIndeks) // Hvis vi har valgt noget på vores movieCategoryListView sker følgende..
            {
                Movie m = (Movie) movieCategoryListView.getItems().get((int) indeks); // Movie m på det indeks vi valgte i vores movieCategoryListView

                // Laver ny stage
                stage = new Stage();
                stage.setTitle(m.getTitle());
                mediaBorder = new BorderPane();

                // Laver ny media som vores MediaPlayer skal afspille. Vi benytter vores getFileFromSelectedMovie metode, der gør at vi får den valgte films fil navn
                Media video = new Media(String.valueOf(getClass().getResource(getFileFromSelectedMovie())));
                moviePlayer = new MediaPlayer(video); // ny MediaPlayer der skal afspille 'video', som er det filNavn der er tilknyttet den film vi har valgt
                moviePlayer.setAutoPlay(true); // Starter automatisk med at afspille videoen når det nye vindue bliver åbnet
                moviePlayer.setOnReady(() -> stage.sizeToScene());
                mediaView = new MediaView(moviePlayer);  // Viser videoen der bliver afspillet
                mediaBorder.setCenter(mediaView);

                scene = new Scene(mediaBorder);
                stage.setScene(scene); // Vores scene som indeholder vores MediaView bliver sat ind i vores stage så vi kan se den
                stage.showAndWait(); // Afventer hvad brugeren gør

                if (!stage.isShowing()){ // Hvis vores stage med vores MediaView ikke bliver vist lige nu sker følgende...
                    moviePlayer.pause(); // Stopper vores moviePlayer så den ikke bliver ved med at afspille videoen
                    mdi.updateLastSeen(dtf.format(now), m.getId()); // Benytter os af vores MovieDaoImpl metode updateLastSeen, der opdaterer vores lastSeen inde i vores database.
                    System.out.println(m.getId());
                    movieCategoryListView.getItems().clear();
                    showMoviesInCategory();
                }
            }
    } // Vores MediaPlayer der afspiller den video der er associeret med filmen vi har valgt

    @FXML
    void addCategory(ActionEvent event) {

        Dialog<ButtonType> addCategoryDialog = new Dialog();

        // Her sættes et nyt vindue op til oprettelse af en Category
        addCategoryDialog.setTitle("Create Category");
        addCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField categoryName = new TextField();
        categoryName.setPromptText("Category name...");
        VBox setup = new VBox(categoryName);
        addCategoryDialog.getDialogPane().setContent(setup);

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = addCategoryDialog.showAndWait();

        // Derefter kan vi hente felternes indhold ud og sætter dem ind hvor de skal ift. databasen og de sql statements vi har lavet i
        // vores CategoryDaoImpl

        if (knap.get() == ButtonType.OK)
            try {
                cdi.addCategory(categoryName.getText()); // Laver vores nye kategori og opretter den i databasen

                List<Category> category = cdi.getAllCategories();
                categoryListView.getItems().clear();
                for (Category newCategory : category) {
                    categoryListView.getItems().add(newCategory); // Tilføjer den nye kategori
                }
            }

            // Laver en catch til hvis Kategorien ikke kan oprettes
            catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
    } // Denne funktion sørger for at vi kan tilføje en ny category

    @FXML
    void addMovie(ActionEvent event) {
        // Sætter et nyt dialog vindue op til at lave en ny film

        Dialog<ButtonType> addMovieDialog = new Dialog<>();

        addMovieDialog.setTitle("Add movie");
        addMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Står for alt der har med Title at gøre
        Label titleLabel = new Label("Title:");
        TextField titleText = new TextField();
        titleText.setPromptText("Insert title...");
        HBox titleHBox = new HBox(titleLabel, titleText);
        titleHBox.setSpacing(10);

        // Står for alt der har med Director at gøre
        Label directorLabel = new Label("Director:");
        TextField directorText = new TextField();
        directorText.setPromptText("Insert director...");
        HBox directorHBox = new HBox(directorLabel, directorText);
        directorHBox.setSpacing(10);

        // Står for alt der har med film beskrivelsen at gøre
        Label movieDescription = new Label();
        TextArea movieDescriptionText = new TextArea();
        movieDescriptionText.setPromptText("Insert movie description (max 200 characters)...");
        movieDescription.setPrefWidth(30);
        movieDescriptionText.wrapTextProperty();
        movieDescriptionText.isWrapText();
        movieDescriptionText.setWrapText(true); // Får teksten til at holde sig inde i boksen når man ud til enden i stedet for at fortsætte ud i evigheden.
        movieDescription.textProperty().bind(movieDescriptionText.textProperty().length().asString("%d")); // Tæller hvor mange tegn man har brugt i real time
        HBox movieDescriptionHBox = new HBox(movieDescription, movieDescriptionText);
        movieDescriptionHBox.setSpacing(20);

        // Står for alt der har med IMDB rating at gøre
        Label IMDBrating = new Label("IMDB rating:");
        TextField IMDBtext = new TextField();
        IMDBtext.setPromptText("Insert IMDB rating (for example, '3.3'");
        HBox IMDBratingHBox = new HBox(IMDBrating, IMDBtext);
        IMDBratingHBox.setSpacing(10);

        // Står for alt der har med vores personlige rating at gøre
        Label ownRating = new Label("Your rating:");
        TextField ownRatingText = new TextField();
        ownRatingText.setPromptText("Insert your own rating (for example, '3.3'");
        HBox YourRatingHBox = new HBox(ownRating, ownRatingText);
        YourRatingHBox.setSpacing(10);

        // Står for alt der har med fil navnet at gøre
        Label fileName = new Label("File:");
        TextField fileNameText = new TextField();
        fileNameText.setPromptText("File.mp4.., mpeg4..");
        fileNameText.setEditable(false);
        Button chooseFile = new Button("Choose.."); // laver ny knap
        chooseFile.setId("fileButton");

        final FileChooser fileChooser = new FileChooser(); // Laver en fileChooser til valg af det medie vi ønsker at tilknytte en film

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mpeg4")); // Gør så brugeren kun kan vælge enten mp4 eller mpeg4 filer.
        chooseFile.setOnAction(e -> { // Hvis man trykker på knappen vi lavede før åbner den så et nyt dialog vindue hvor man så skal vælge den fil man skal bruge
            try
            {
                Stage stage = (Stage) bigPane.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage);
                fileNameText.setText(file.getName());
                file.renameTo(new File("/Users/MadMe/IdeaProjects//Filmaficionado eksamen projekt/src/main/resources/com/example/filmaficionado_eksamen_projekt/" + file.getName()));

            }
            catch (Exception ex) {}
        });

        HBox fileNameHBox = new HBox(fileName, fileNameText, chooseFile);
        fileNameHBox.setSpacing(10);

        // Sætter hele vores tilføj film dialog vindue op
        VBox setup = new VBox(titleHBox, directorHBox, movieDescriptionHBox, IMDBratingHBox, YourRatingHBox, fileNameHBox);
        setup.setSpacing(10);
        addMovieDialog.getDialogPane().setContent(setup);
        Optional<ButtonType> knap = addMovieDialog.showAndWait();


        // Hvis bruger trykker ok OG opfylder de krav der er, dvs. at der ikke er nogle tomme felter OG at film beskrivelsen er mindre en eller lig med 200 tegn, så tilføjes den nye film til databasen
        if (knap.get() == ButtonType.OK && movieDescriptionText.getLength() < 200 && titleText.getLength() > 0 && directorText.getLength() > 0 && IMDBtext.getLength() > 0 && ownRatingText.getLength() > 0 && fileNameText.getLength() > 0)
            try{
                // Tager teksten som er i de forskellige text fields og opretter den nye sang
                mdi.addMovie(titleText.getText(), directorText.getText(), movieDescriptionText.getText(), IMDBtext.getText(), ownRatingText.getText(), fileNameText.getText(), dtf.format(now), String.valueOf(0));

                List<Movie> movies = mdi.getAllMovies();
                movieListView.getItems().clear();
                for (Movie mov : movies){
                    movieListView.getItems().add(mov);
                }
                movieListView.scrollTo(movieListView.getItems().size());
            }
            catch (Exception e)
            {System.err.println("Fejl: " + e.getMessage());}

        else if (knap.get() == ButtonType.CANCEL) {} // Hvis man trykker cancel sker der ikke andet end vinduet lukkes ned

        else { // Hvis de ting der er blevet sat ind i tekst felterne ikke opfylder de krav vi har opstillet fra før sker følgende...

            // Et nyt dialog vindue bliver sat op, der fortæller brugeren at der er blevet begået en fejl under tilføjelsen af den nye film
            Dialog<ButtonType> errorDialog = new Dialog<>();
            errorDialog.setTitle("Error");
            errorDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please make sure your movie description is max 200 characters long and that you did not leave any TextFields empty");
            errorDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = errorDialog.showAndWait();
        }
    } // Tilføjer en film til databasen ved at benytte os af vores MovieDaoImpl metode. Vi bruger her blandt andet en fileChooser der finder filen der skal tilknyttes en film.
                                           // Hvis der opstår fejl under processen vil brugeren blive underrettet.

    @FXML
    void addMovieToCategory(ActionEvent event) {

        ObservableList valgteIndeks = movieListView.getSelectionModel().getSelectedIndices();
        ObservableList valgteIndeks2 = categoryListView.getSelectionModel().getSelectedIndices();

        if (valgteIndeks.size() == 0) { // Hvis vi ikke har valgt noget sker følgende...
            for (Object indeks : valgteIndeks2) {

                Category cat = (Category) categoryListView.getItems().get((int) indeks); // Cat = den valgte category, vi bruger den til at få navnet på kategorien

                // Nyt dialog vindue bliver sat op. Viser brugeren at han skal vælge en film at tilføje til den valgte kategori.
                Dialog<ButtonType> selectAMovieToAddDialog = new Dialog<>();
                selectAMovieToAddDialog.setTitle("Select a movie");
                selectAMovieToAddDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                Label selectCategoryLabel = new Label("Please select a movie to add to " + cat.getCategoryName());
                selectAMovieToAddDialog.getDialogPane().setContent(selectCategoryLabel);
                Optional<ButtonType> knap1 = selectAMovieToAddDialog.showAndWait();
            }
        }

        if (valgteIndeks2.size() == 0){
            for (Object indeks : valgteIndeks) {

                Movie mov = (Movie) movieListView.getItems().get((int) indeks); // mov = den valgte movie, vi bruger den til at få navnet på filmen

                // Nyt dialog vindue bliver sat op. Viser brugeren at han skal vælge en kategori før han kan tilføje en film til den.
                Dialog<ButtonType> selectACategoryToAddDialog = new Dialog<>();
                selectACategoryToAddDialog.setTitle("Select a category");
                selectACategoryToAddDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                Label selectCategoryLabel = new Label("Please select a category to add " + mov.getTitle() + " to");
                selectACategoryToAddDialog.getDialogPane().setContent(selectCategoryLabel);
                Optional<ButtonType> knap1 = selectACategoryToAddDialog.showAndWait();
            }
        }
        else {
            for (Object indeks : valgteIndeks) // Hvis man rent faktisk har valgt både en category og en movie sker følgende..
            {
                Movie mov = (Movie) movieListView.getItems().get((int) indeks); // Får den valgte film
                Category cat = (Category) categoryListView.getSelectionModel().getSelectedItem(); // Får den valgte category
                cmdi.addMovieToCategory(mov.getId(), cat.getId(), mov.getFavorite()); // Tilknytter en film til en kategori ved at benytte en metode fra vores CategoryMovieDaoImpl

                movieCategoryListView.getItems().clear();
                List<Movie> moviesInCategory = cmdi.getMoviesInCategory(cat); // Får filmene i den valgte kategori
                for (Movie mo : moviesInCategory)
                        movieCategoryListView.getItems().add(mo); // Tilføjer filmene der er tilknyttet til den valgte kategori
                }
            }
        } // Tilføjer en film til en valgt kategori. Hvis intet er valgt, vil brugeren få en fejl meddelelse.

    @FXML
    void editMovie(ActionEvent event) {

        ObservableList tjekOmValgt = movieListView.getSelectionModel().getSelectedIndices();

        if (tjekOmValgt.size() == 0) { // Hvis der ikke er valgt en film at redigere sker følgende..

            // Nyt dialog vindue bliver sat op der informerer brugeren om at der ikke er valgt en film at redigere
            Dialog<ButtonType> selectCategoryDialog = new Dialog<>();
            selectCategoryDialog.setTitle("Select a movie");
            selectCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please select a valid movie to edit");
            selectCategoryDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectCategoryDialog.showAndWait();
        }
        else {
            for (Object indeks : tjekOmValgt) // Hvis der er blevet valgt en film sker følgende..
            {
                Movie m = (Movie) movieListView.getItems().get((int) indeks); // Får den valgte film

                // Nyt dialog vindue bliver sat op. Den er helt magen til vores addMovie dialog vindue.
                Dialog<ButtonType> editMovieDialog = new Dialog<>();

                editMovieDialog.setTitle("Edit movie");
                editMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                // Står for alt der har med Title at gøre
                Label titleLabel = new Label("Title:");
                TextField titleText = new TextField();
                titleText.setText(m.getTitle()); // Indsætter den title den valgte film allerede har
                HBox titleHBox = new HBox(titleLabel, titleText);
                titleHBox.setSpacing(10);

                // Står for alt der har med Director at gøre
                Label directorLabel = new Label("Director:");
                TextField directorText = new TextField();
                directorText.setText(m.getDirector()); // Indsætter den director den valgte film allerede har
                HBox directorHBox = new HBox(directorLabel, directorText);
                directorHBox.setSpacing(10);

                // Står for alt der har med film beskrivelsen at gøre
                Label movieDescription = new Label();
                TextArea movieDescriptionText = new TextArea();
                movieDescriptionText.setText(m.getMovieDescription()); // Indsætter den film beskrivelse den valgte film allerede har
                movieDescription.setPrefWidth(30);
                movieDescription.textProperty().bind(movieDescriptionText.textProperty().length().asString("(%d):"));
                HBox movieDescriptionHBox = new HBox(movieDescription, movieDescriptionText);
                movieDescriptionHBox.setSpacing(20);

                // Står for alt der har med IMDB rating at gøre
                Label IMDBrating = new Label("IMDB rating:");
                TextField IMDBtext = new TextField();
                IMDBtext.setText(String.valueOf(m.getImdbRating())); // Indsætter den IMDB rating den valgte film allerede har
                HBox IMDBratingHBox = new HBox(IMDBrating, IMDBtext);
                IMDBratingHBox.setSpacing(10);

                // Står for alt der har med vores personlige rating at gøre
                Label ownRating = new Label("Your rating:");
                TextField ownRatingText = new TextField();
                ownRatingText.setText(String.valueOf(m.getMyRating())); // Indsætter den rating vi gav den da vi oprettede filmen
                HBox YourRatingHBox = new HBox(ownRating, ownRatingText);
                YourRatingHBox.setSpacing(10);

                // Står for alt der har med fil navnet at gøre
                Label fileName = new Label("File:");
                TextField fileNameText = new TextField();
                fileNameText.setText(m.getFileName()); // Indsætter den title den fil den valgte film allerede har
                Button chooseFile = new Button("Choose..");
                chooseFile.setId("fileButton");

                final FileChooser fileChooser = new FileChooser(); // Laver en fileChooser til valg af det medie vi ønsker at tilknytte en film

                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mpeg4")); // Gør så brugeren kun kan vælge enten mp4 eller mpeg4 filer.
                chooseFile.setOnAction(e -> { // Hvis man trykker på knappen vi lavede før åbner den så et nyt dialog vindue hvor man så skal vælge den fil man skal bruge
                    try
                    {
                        Stage stage = (Stage) bigPane.getScene().getWindow();
                        File file = fileChooser.showOpenDialog(stage);
                        fileNameText.setText(file.getName());
                        file.renameTo(new File("/Users/MadMe/IdeaProjects//Filmaficionado eksamen projekt/src/main/resources/com/example/filmaficionado_eksamen_projekt/" + file.getName()));

                    }
                    catch (Exception ex) {}
                });

                HBox fileNameHBox = new HBox(fileName, fileNameText, chooseFile);
                fileNameHBox.setSpacing(10);
                // Opsætter vores editMovie dialog vindue
                VBox setup = new VBox(titleHBox, directorHBox, movieDescriptionHBox, IMDBratingHBox, YourRatingHBox, fileNameHBox);
                setup.setSpacing(10);
                editMovieDialog.getDialogPane().setContent(setup);
                Optional<ButtonType> knap = editMovieDialog.showAndWait();

                ObservableList lastCategory = categoryListView.getSelectionModel().getSelectedIndices(); // Får den valgte kategori man sidst havde fat i

                if (knap.get() == ButtonType.OK && movieDescriptionText.getLength() < 200 && titleText.getLength() > 0 && directorText.getLength() > 0 && IMDBtext.getLength() > 0 && ownRatingText.getLength() > 0 && fileNameText.getLength() > 0) // Hvis den ændring vi har foretaget os opfylder vores krav så sker følgende..
                    try {
                        // Laver de ændringer vi lige har foretaget os
                        mdi.editMovie(titleText.getText(), directorText.getText(), movieDescriptionText.getText(), IMDBtext.getText(), ownRatingText.getText(), fileNameText.getText(), dtf.format(now), String.valueOf(m.getFavorite()),m.getId());
                        List<Movie> movies = mdi.getAllMovies();
                        movieListView.getItems().clear(); // Clearer movieListViewet. Det gør vi for at den ændring vi har foretaget også rent faktisk bliver vist visuelt.
                        movieCategoryListView.getItems().clear(); // Clearer de film der er i movieCategoryListViewet. Det gør vi for at den ændring vi har foretaget os bliver opdateret med det samme, så man ikke skal trykke på en anden category for at det sker.

                        for (Movie mov : movies)
                            movieListView.getItems().add(mov); // Tilføjer alle film til movieListViewet

                        for (Object indeks1 : lastCategory){

                            Category cat = (Category) categoryListView.getItems().get((int) indeks1); // Tager den category som vi sidst havde fat i
                            List<Movie> moviesInCategory = cmdi.getMoviesInCategory(cat);

                            for (Movie mov : moviesInCategory)
                                    movieCategoryListView.getItems().add(mov); // Tilføjer de film der var i den kategori der sidst blev valgt - og altså er den liste af film der bliver vist i movieCategoryListViewet
                            }
                    }
                    catch (Exception e) {System.err.println("Fejl: " + e.getMessage());}
            }
        }
    }  // Gør så man kan redigere en film der er blevet valgt af os. Hvis man ikke har valgt noget vil brugeren blive gjort opmærksom på det.

    @FXML
    public void editCategory(ActionEvent actionEvent) {

        ObservableList tjekOmValgt = categoryListView.getSelectionModel().getSelectedIndices();

        if (tjekOmValgt.size() == 0) { // Hvis vi ikke har valgt en kategori sker følgende..

            // Nyt dialog vindue bliver sat op der fortæller brugeren at der skal vælges en kategori
            Dialog<ButtonType> selectCategoryDialog = new Dialog<>();
            selectCategoryDialog.setTitle("Select a category");
            selectCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please select a valid category to edit");
            selectCategoryDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectCategoryDialog.showAndWait();
        }

        else { // Hvis der er blevet valgt en kategori sker følgende..

            // Nyt dialog vindue bliver sat op
            Dialog<ButtonType> editCategoryDialog = new Dialog();
            editCategoryDialog.setTitle("Edit category");
            editCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            TextField newCategoryName = new TextField();
            newCategoryName.setPromptText("Category name...");
            VBox setup = new VBox(newCategoryName);
            editCategoryDialog.getDialogPane().setContent(setup);
            Optional<ButtonType> knap2 = editCategoryDialog.showAndWait();

            ObservableList valgteIndeks = categoryListView.getSelectionModel().getSelectedIndices();

            for (Object indeks : valgteIndeks) {
                Category cat = (Category) categoryListView.getItems().get((int) indeks); // Cat = den valgte kategori
                cdi.updateCategory(newCategoryName.getText(), cat.getId()); // Opdaterer kategoriens navn i vores database ved at bruge en metode fra vores CategoryDaoImpl
                categoryListView.getItems().clear(); // Clearer vores categoryListView. Gør så at opdateringen kan ses visuelt med det samme
                showCategories(); // Indsætter alle kategorier der findes
            }
        }
    } // Gør så man kan redigere en kategori. Hvis man gør noget forkert vil brugeren blive gjort opmærksom på det.

    @FXML
    public void viewMovieInfo(MouseEvent mouseEvent) {

        ObservableList checkIfChosen = movieCategoryListView.getSelectionModel().getSelectedIndices();
        if (checkIfChosen.size() == 0) // Hvis der ikke er valgt en film i movieCategoryListViewet sker følgende..
        {
            // Nyt dialog vindue bliver sat op. Informerer brugeren om at der skal vælges en film
            Dialog<ButtonType> selectMovieInfoDialog = new Dialog<>();
            selectMovieInfoDialog.setTitle("Select a movie");
            selectMovieInfoDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Select a movie to see its info");
            selectMovieInfoDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectMovieInfoDialog.showAndWait();
        }

        else {
            for (Object indeks : checkIfChosen) // Hvis der er valgt en film sker følgende...
            {
                Movie m = (Movie) movieCategoryListView.getItems().get((int) indeks);

                // Nyt dialog vindue bliver sat op med alt den information der er på filmen.
                Dialog<ButtonType> movieInfo = new Dialog<>();
                movieInfo.setTitle(m.getTitle() + " info");
                movieInfo.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                // Viser information om director
                Label directorLabel = new Label("Director:");
                TextField directorText = new TextField();
                directorText.setEditable(false);
                directorText.setText(m.getDirector());
                HBox directorHBox = new HBox(directorLabel, directorText);
                directorHBox.setSpacing(10);

                // Viser film beskrivelsen
                Label movieDescription = new Label("Info");
                TextArea movieDescriptionText = new TextArea();
                movieDescriptionText.setText(m.getMovieDescription());
                movieDescription.setPrefWidth(30);
                movieDescriptionText.setEditable(false);
                HBox movieDescriptionHBox = new HBox(movieDescription, movieDescriptionText);
                movieDescriptionHBox.setSpacing(20);

                // Viser IMDB rating
                Label IMDBrating = new Label("IMDB rating: ");
                TextField IMDBtext = new TextField();
                IMDBtext.setEditable(false);
                IMDBtext.setText(String.valueOf(m.getImdbRating()));
                HBox IMDBratingHBox = new HBox(IMDBrating, IMDBtext);
                IMDBratingHBox.setSpacing(10);

                // Viser vores personlige rating
                Label ownRating = new Label("Your rating: ");
                TextField ownRatingText = new TextField();
                ownRatingText.setEditable(false);
                ownRatingText.setText(String.valueOf(m.getMyRating()));
                HBox YourRatingHBox = new HBox(ownRating, ownRatingText);
                YourRatingHBox.setSpacing(10);

                // Viser information om hvornår man sidst så filmen
                Label seenLast = new Label("Last seen: ");
                TextField date = new TextField();
                date.setEditable(false);
                date.setText(String.valueOf(m.getLastSeen()));
                HBox lastSeenDate = new HBox(seenLast,date);
                lastSeenDate.setSpacing(10);

                VBox setup = new VBox(directorHBox,movieDescriptionHBox,IMDBratingHBox,YourRatingHBox,lastSeenDate);
                movieInfo.getDialogPane().setContent(setup);
                Optional<ButtonType> knap = movieInfo.showAndWait();
            }
        }

    } // Viser information om den film man har valgt fra movieCategoryListViewet

    @FXML
    void favoriteMovie(ActionEvent event) {

        ObservableList valgteIndeks = movieCategoryListView.getSelectionModel().getSelectedIndices();
        ObservableList valgteIndeks2 = categoryListView.getSelectionModel().getSelectedIndices();

        if (valgteIndeks.size() == 0) { // Hvis der ikke er valgt en film i movieCategoryListViewet sker følgende...

            // Nyt dialog vindue bliver sat op. Informerer brugeren om at man skal vælge en film
            Dialog<ButtonType> selectAMovieToAddDialog = new Dialog<>();
            selectAMovieToAddDialog.setTitle("Select a movie");
            selectAMovieToAddDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please select a movie to favorite");
            selectAMovieToAddDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectAMovieToAddDialog.showAndWait();
        }
        if (valgteIndeks2.size() == 0) { // Hvis der ikke er valgt en kategori i categoryListViewet sker følgende...

            // Nyt dialog vindue bliver sat op. Informerer brugeren om at man skal vælge en kategori
            Dialog<ButtonType> selectACategoryToAddDialog = new Dialog<>();
            selectACategoryToAddDialog.setTitle("Select a category");
            selectACategoryToAddDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please select a category before favoriting a movie");
            selectACategoryToAddDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectACategoryToAddDialog.showAndWait();

        } else {
            for (Object indeks : valgteIndeks) {

                Movie m = (Movie) movieCategoryListView.getItems().get((int) indeks); // Den valgte film i movieCategoryListView
                Category cat = (Category) categoryListView.getSelectionModel().getSelectedItem(); // Den valgte category i categoryListView

                cmdi.addToFavorite(m.getId(), cat.getId()); // Markerer den valgte film i den valgte playliste som favorit

                favoriteMovieInCategoryListView.getItems().clear(); // clearer favoritMovieInCategoryListView
                List<Movie> movieFavoritesInCategory = cmdi.getMovieFavorites(cat); // Får den favorit film / den bedste film i den valgte kategori
                for (Movie mo : movieFavoritesInCategory){
                    favoriteMovieInCategoryListView.getItems().add(mo); // Indsætter favoritten/den bedste film i en speciel liste
                }
            }
        }
    } // Når man trykker på den markerer man en film som favorit / den bedste film i en valgt kategori

    @FXML
    void removeCategory(ActionEvent event) {

        ObservableList tjekOmValgt = categoryListView.getSelectionModel().getSelectedIndices();

        if (tjekOmValgt.size() == 0) { // Hvis man ikke har valgt en kategori fra categoryListViewet sker følgende..

            // Nyt dialog vindue bliver sat op og informerer brugeren om at man skal vælge en kategori
            Dialog<ButtonType> selectCategoryDialog = new Dialog<>();
            selectCategoryDialog.setTitle("Select a category");
            selectCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectCategoryLabel = new Label("Please select a valid category to remove");
            selectCategoryDialog.getDialogPane().setContent(selectCategoryLabel);
            Optional<ButtonType> knap1 = selectCategoryDialog.showAndWait();
        }
        else { // Hvis man har valgt en kategori sker følgende..

            // Nyt dialog vindue bliver sat om. Spørger brugeren om han vil fortsætte
            Dialog<ButtonType> removeCategoryDialog = new Dialog();
            removeCategoryDialog.setTitle("Remove category");
            removeCategoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Label infoLabel = new Label("Are you sure you want to continue removing " + categoryListView.getSelectionModel().getSelectedItem().toString() + "?");
            removeCategoryDialog.getDialogPane().setContent(infoLabel);
            Optional<ButtonType> knap2 = removeCategoryDialog.showAndWait();

            if (knap2.get() == ButtonType.OK) { // Hvis brugeren trykker ok sker følgende..
                ObservableList valgteIndeks = categoryListView.getSelectionModel().getSelectedIndices();

                for (Object indeks : valgteIndeks) {
                    Category cat = (Category) categoryListView.getItems().get((int) indeks); // Får den valgte kategori
                    cdi.removeCategory(cat.getId()); // Fjerner kategorien fra vores database ved hjælp af en metode fra vores CategoryDaoImpl
                    categoryListView.getItems().clear(); // Clearer vores categoryListView så den kan opdateres i real time.
                    showCategories(); // Indsætter alle kategorier der findes.
                }
            }
        }
    } // Fjerner en valgt category fra listen. Hvis brugeren IKKE har valgt en category, vil brugeren få det at vide.

    @FXML
    void removeMovie(ActionEvent event) {

        ObservableList tjekOmValgt = movieListView.getSelectionModel().getSelectedIndices();

        if (tjekOmValgt.size() == 0) { // Hvis der ikke er blevet valgt en film i vores movieListView sker der følgende..

            // Nyt dialog vindue bliver sat op og informerer brugeren om at der skal vælges en sang.
            Dialog<ButtonType> selectMovieDialog = new Dialog<>();
            selectMovieDialog.setTitle("Select a movie");
            selectMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectMovieLabel = new Label("Please select a valid movie to remove");
            selectMovieDialog.getDialogPane().setContent(selectMovieLabel);
            Optional<ButtonType> knap1 = selectMovieDialog.showAndWait();
        }
        else {
            for (Object indeks : tjekOmValgt) { // Hvis der er valgt en film på vores movieListView sker der følgende..
                Movie m = (Movie) movieListView.getItems().get((int) indeks);

                // Nyt dialog vindue bliver sat op der spørger brugeren om de er sikker på de vil fjerne filmen
                Dialog<ButtonType> removeMovieDialog = new Dialog();
                removeMovieDialog.setTitle("Remove movie");
                removeMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                Label infoLabel = new Label("Are you sure you want to continue removing " + m.getTitle() + "?");
                removeMovieDialog.getDialogPane().setContent(infoLabel);
                Optional<ButtonType> knap2 = removeMovieDialog.showAndWait();

                if (knap2.get() == ButtonType.OK) { // Hvis de trykker ja, sker følgende
                    ObservableList valgteIndeks2 = movieListView.getSelectionModel().getSelectedIndices();
                    ObservableList lastCategory = categoryListView.getSelectionModel().getSelectedIndices(); // Den kategori der er valgt

                    for (Object indeks2 : valgteIndeks2) {
                        Movie mov = (Movie) movieListView.getItems().get((int) indeks2); // Får den valgte film
                        mdi.removeMovie(mov.getId()); // Fjerner filmen fra vores database med film - OG fra de steder hvor en film er tilknyttet en kategori
                        movieListView.getItems().clear(); // Clearer movieListView
                        movieCategoryListView.getItems().clear(); // Clearer movieCategoryListView
                        showMovies(); // Viser alle film der findes

                        for (Object indeks1 : lastCategory){

                            Category cat = (Category) categoryListView.getItems().get((int) indeks1); // Valgte kategori
                            List<Movie> moviesInCategory = cmdi.getMoviesInCategory(cat);

                            for (Movie mo : moviesInCategory)
                                movieCategoryListView.getItems().add(mo); // Tilføjer alle de film der er i den valgte kategori
                        }
                    }
                }
            }
        }
    } // Fjerner en valgt film. Filmen fjernes fra databasen med film OG de steder hvor en film er tilknyttet en kategori. Hvis man ikke har valgt en film får man det at vide.

    @FXML
    public void removeMovieFromCategory(ActionEvent actionEvent) {

        ObservableList valgteIndeks = movieCategoryListView.getSelectionModel().getSelectedIndices();

        if(valgteIndeks.size() == 0) // Hvis der ikke er valgt noget på movieCategoryListViewet sker følgende..
        {
            // Nyt dialog vindue sættes op. Brugeren bliver informeret om at der skal vælges en film.
            Dialog<ButtonType> selectMovieToRemoveDialog = new Dialog<>();
            selectMovieToRemoveDialog.setTitle("Select a movie");
            selectMovieToRemoveDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            Label selectMovieLabel = new Label("Please select a valid movie to remove");
            selectMovieToRemoveDialog.getDialogPane().setContent(selectMovieLabel);
            Optional<ButtonType> knap1 = selectMovieToRemoveDialog.showAndWait();
        }
        else {
            for (Object indeks3 : valgteIndeks) {

                Movie mov = (Movie) movieCategoryListView.getItems().get((int) indeks3); // Får den valgte film på movieCategoryListViewet
                Category cat = (Category) categoryListView.getSelectionModel().getSelectedItem(); // Får den valgte kategori
                cmdi.removeMovieFromCategory(mov.getId(),cat.getId()); // Fjerner filmen fra kategorien ved hjælp af vores CategoryMovieDaoImpl.

                favoriteMovieInCategoryListView.getItems().clear(); // Clearer favoriteMovieInCategoryListViewet - hvis det nu var at filmen var i den kategori
                List<Movie> movieFavorites = cmdi.getMovieFavorites(cat); // Får favoritterne / den bedste film i den valgte kategori

                for(Movie mo : movieFavorites)
                    favoriteMovieInCategoryListView.getItems().add(mo); // Indsætter favoritterne / den bedste film
            }
        }


        ObservableList chosenCat = categoryListView.getSelectionModel().getSelectedIndices();
        if (chosenCat.size() == 0)
        {
            Dialog<ButtonType> selectCategoryFirst = new Dialog<>();

            selectCategoryFirst.setTitle("Select a category");

            selectCategoryFirst.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            Label selectMovieLabel = new Label("Please select a valid category");

            selectCategoryFirst.getDialogPane().setContent(selectMovieLabel);

            Optional<ButtonType> knap1 = selectCategoryFirst.showAndWait();
        }
        else
            for(Object indeks : chosenCat)
            {
                movieCategoryListView.getItems().clear();
                Category cat = (Category) categoryListView.getItems().get((int) indeks);
                List<Movie> movies = cmdi.getMoviesInCategory(cat);
                for (Movie mov : movies)
                {
                    movieCategoryListView.getItems().add(mov);}
            }
    } // Fjerner en film fra en kategori. Fjernes i databasen.

    @FXML
    void searchButton(ActionEvent event) {

        if (searchCounter == 0){    // Hvis searchCounter == 0 så søger den når man trykker på searchButton..

            List<Movie> getSearchedSongs = mdi.searchMovie(søgFelt.getText()); // Søger i databasen ved hjælp af vores MovieDaoImpl
            movieListView.getItems().clear();
            for(Movie mo: getSearchedSongs)
                movieListView.getItems().add(mo);    // Sætter de film ind i movieListView der passede med det man søgte på.

            filterMode.setText("Filter mode: on");    // Ændrer på label tekst
            searchCounter = 1;    // Sætter searchCounter til 1

        } else if (searchCounter == 1) {    // Når searchCounter == 1 så sker følgende når man trykker på searchButton..

            søgFelt.setText("");    // Clearer søgeFeltet
            movieListView.getItems().clear();    // Clearer movieListViewet
            showMovies();    // Indsætter alle film. Man kan sige at man resetter søgningen
            filterMode.setText("Filter mode: off");    // Sætter teksten
            searchCounter = 0;    // Sætter searchCounter = 0, dvs. næste gang man trykker på searchButton vil den søge på det man skriver ind i søgeFeltet
        }
    } // Gør at man kan søge på det meste. Man kan søge på film title, IMDB rating, og så kan man også søge på en kategori.

    public void filterByIMDB(ActionEvent actionEvent) {
        if (searchCounter == 0){

            List<Movie> filteredByIMDBMovies = mdi.filterByIMDB(søgFelt.getText()); // Søger i databasen og filtrerer så det den får tilbage efter IMDB rating
            movieListView.getItems().clear();
            for(Movie mo: filteredByIMDBMovies)
                movieListView.getItems().add(mo); // Indsætter filmene i den rækkefølge som matcher filtreringen

            filterMode.setText("Filter mode: on");
            searchCounter = 1;

        } else if (searchCounter == 1) {
            søgFelt.setText("");
            movieListView.getItems().clear(); // Clearer movieListView og resetter det hele
            showMovies();
            filterMode.setText("Filter mode: off");
            searchCounter = 0;
        }
    } // Filtrerer en søgning udelukkende på IMDB rating. Tager den højeste til den laveste.

    public void filterByCategory(ActionEvent actionEvent) {
        if (searchCounter == 0){

            List<Movie> filteredByIMDBCategory = mdi.filterByCategory(søgFelt.getText()); // Filtrerer film efter kategori vha. MovieDaoImpl
            movieListView.getItems().clear();
            for(Movie mo: filteredByIMDBCategory)
                movieListView.getItems().add(mo); // Får alle film sat ind i den rækkefølge der matcher søgningen + filtreringen

            filterMode.setText("Filter mode: on");
            searchCounter = 1;

        } else if (searchCounter == 1) {
            søgFelt.setText("");
            movieListView.getItems().clear();
            showMovies();
            filterMode.setText("Filter mode: off");
            searchCounter = 0;
        }
    } // Filtrerer en søgning udelukkende på Kateogori. Alfabetisk filtreret efter kategori navn.

    public void filterByTitle(ActionEvent actionEvent) {
        if (searchCounter == 0){

            List<Movie> filteredByIMDBTitles = mdi.filterByTitle(søgFelt.getText()); // Filtrerer film efter titel. Alfabetisk filtrering, A-Z.
            movieListView.getItems().clear();
            for(Movie mo: filteredByIMDBTitles)
                movieListView.getItems().add(mo); // Får alle film sat ind i den rækkefølge der matcher søgningen + filtreringen

            filterMode.setText("Filter mode: on");
            searchCounter = 1;

        } else if (searchCounter == 1) {
            søgFelt.setText("");
            filterMode.setText("Filter mode: off");
            movieListView.getItems().clear();
            showMovies();
            searchCounter = 0;
        }
    } // Filtrerer en søgning udelukkende efter titel. Alfabetisk filtrering A-Z.

    public void notificationText(){
        if (searchCounter == 0)
            filterMode.setText("Filter mode: off"); // Sætter teksten fra starten af. Fortæller om et filter er sat til eller ej.
    } // Viser tekst der fortæller om et filter er til eller ej. Er forbundet med filtrene for oven.

    MovieDAO mdi = new MovieDaoImpl();
    CategoryDAO cdi = new CategoryDaoImpl();
    CategoryMovieDAO cmdi = new CategoryMovieDaoImpl();
}
