package nl.hva.ict.data.MongoDB;

import com.mongodb.client.model.Accumulators;
import nl.hva.ict.MainApplication;
import nl.hva.ict.models.Landen;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;


/**
 * Landen informatie ophalen van de MongoDB
 */
public class MongoLandeninformatie extends MongoDB {

    private final List<Landen> landen;

    /**
     * Constructor
     */
    public MongoLandeninformatie() {
        // Init arraylist
        landen = new ArrayList<>();
    }

    /**
     * Haal alle landen op die in de arraylijst zitten
     * @return arraylijst met landen
     */
    @Override
    public List getAll() {
        return landen;
    }

    /**
     * Haal 1 object op. Niet gebruikt in deze class maar door de interface data wel verplicht
     * @return een object
     */
    @Override
    public Object get() {
        return null;
    }

    /**
     * Voeg een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void add(Object object) {

    }

    /**
     * Update een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void update(Object object) {

    }

    /**
     * Verwijder een object toe aan de arraylist. Niet gebruikt in deze class maar door de interface data wel verplicht
     */
    @Override
    public void remove(Object object) {

    }

    /**
     * Haal alle informatie op uit de NoSQL server over welke landen een bepaalde taal spreken. Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param taal Welke taal wil je weten
     * @param alleenAfrika filter het resultaat zodat wel of niet alleen afrikaanse landen terug komen
     */
    public void wieSpreekt(String taal, boolean alleenAfrika) {
        // Als je geen NoSQL server hebt opgegeven gaat de methode niet verder anders zou je een nullpointer krijgen
        if (MainApplication.getNosqlHost().equals(""))
            return;

        // reset arraylist
        this.landen.clear();

        // selecteer collection
        this.selectedCollection("Landen");

        List<Bson> aggregationPipeline = new ArrayList<>();

        // Voeg match toe voor de opgegeven taal
        aggregationPipeline.add(match(eq("languages.name", taal)));

        // Voeg match toe voor det checkbox
        if (alleenAfrika) {
            aggregationPipeline.add(match(eq("region", "Africa")));
        }

        // Voer de aggregatie uit
        List<Document> results = collection.aggregate(aggregationPipeline)
                .into(new ArrayList<>());

        // Maak models en voeg resultaat toe aan arraylist
        for (Document land : results) {
            this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
        }
    }


    /**
     * Haal alle informatie op uit de NoSQL server in welke landen je met een bepaalde valuta kan betalen. Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param valuta Welke valuta wil je weten
     * @param alleenAfrika filter het resultaat zodat wel of niet alleen afrikaanse landen terug komen
     */
    public void waarBetaalJeMet(String valuta, boolean alleenAfrika) {
        // Check if NoSQL server is specified
        if (MainApplication.getNosqlHost().equals(""))
            return;

        // Reset the arraylist
        this.landen.clear();

        // Select the collection
        this.selectedCollection("Landen");

        // Create the aggregation pipeline
        List<Bson> pipeline = new ArrayList<>();

        // Match documents that have the specified currency
        Bson match = match(eq("currencies.name", valuta));
        pipeline.add(match);

        // Optionally, filter documents based on continent
        if (alleenAfrika) {
            Bson africaFilter = match(eq("region", "Africa"));
            pipeline.add(africaFilter);
        }

        // Execute the aggregation pipeline
        List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());

        // Create models and add results to the arraylist
        for (Document land : results) {
            this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
        }
    }


    /**
     * Welke landen zijn er in welk werelddeel. Haal deze informatie uit de database
     * . Gebruik hiervoor aggregation.
     * Zet het resultaat in de arraylist
     * @param werelddeel Welke valuta wil je weten
     */
    public void welkeLandenZijnErIn(String werelddeel) {
        // If you haven't specified a NoSQL server, the method won't proceed to avoid a null pointer exception
        if (MainApplication.getNosqlHost().equals("")) {
            return;
        }

        // Reset the ArrayList
        this.landen.clear();

        // Select the collection
        this.selectedCollection("Landen");

        // Create the aggregation pipeline
        List<Bson> pipeline = new ArrayList<>();

        // Match documents that have the specified continent
        Bson match = match(or( eq("subregion", werelddeel), eq("region", werelddeel)));
        pipeline.add(match);

        // Execute the aggregation pipeline
        List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());

        // Create models and add the results to the ArrayList
        for (Document land : results) {
            this.landen.add(new Landen(land.get("name").toString(), land.get("capital").toString()));
        }
    }


    /**
     * Hoeveel inwoners heeft Oost-Afrika?. Haal deze informatie uit de database en gebruik hiervoor aggregation.
     */
    public int hoeveelInwonersOostAfrika() {
        if (MainApplication.getNosqlHost().equals("")) {
            return 0;
        }

        this.selectedCollection("Landen");

        Document fillter = new Document("subregion", "Eastern Africa");
        long count = collection.countDocuments(fillter);

        return (int) count;
    }

}