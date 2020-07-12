package rip.bolt.ingame.xml;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import rip.bolt.ingame.Tournament;
import rip.bolt.ingame.format.RoundReferenceHolder;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.TournamentFormatImpl;
import rip.bolt.ingame.format.TournamentRoundOptions;
import rip.bolt.ingame.format.winner.BestOfCalculation;

public class MapFormatXMLParser {

    public static TournamentFormat parse(String name) {
        File poolsFolder = new File(Tournament.get().getDataFolder(), "formats");
        File xmlFile = new File(poolsFolder, name + ".xml");

        try {
            Document document = new SAXBuilder().build(xmlFile);
            Element root = document.getRootElement();

            String bestOfArgs = root.getAttributeValue("best-of");
            if (bestOfArgs == null)
                throw new IllegalArgumentException("No best-of specified on format!");
            int bestOf = Integer.parseInt(bestOfArgs);

            TournamentRoundOptions options = new TournamentRoundOptions(false, true, true, Duration.ofSeconds(20), Duration.ofSeconds(30), Duration.ofSeconds(40), new BestOfCalculation<>(bestOf));
            TournamentFormat format = new TournamentFormatImpl(Tournament.get().getTeamManager(), options, new RoundReferenceHolder());

            if (!root.getName().toLowerCase().equals("format"))
                System.out.println("Expecting root element to be format. Got " + root.getName() + " instead!");

            for (Element round : root.getChildren())
                format.addRound(RoundParser.parse(format, round));

            return format;
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}