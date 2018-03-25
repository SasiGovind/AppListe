package upec.projetandroid2017_2018.Elements;

/**
 * Created by Sasig on 22/03/2018.
 */

public class ElementData {
    private String elementName;
    private String description;
    private String username;
    private int ecolor;
    private int priority;
    private int fait;

    public ElementData(String elementName, String description, String username, int ecolor, int priority, int fait) {
        this.elementName = elementName;
        this.description = description;
        this.username = username;
        this.ecolor = ecolor;
        this.priority = priority;
        this.fait = fait;
    }

    public String getElementName() {
        return elementName;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public int getEcolor() {
        return ecolor;
    }

    public int getPriority() {
        return priority;
    }

    public int getFait() {
        return fait;
    }
}
