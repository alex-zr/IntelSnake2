package jon.com.ua.pathfind;

import jon.com.ua.view.Element;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 12/27/13
 */
public class DirectionCompass {
    Element rightElement;
    Element leftElement;
    Element downElement;
    Element upElement;

    public DirectionCompass(Element sourceElement) {
        this.rightElement = new Element(Color.BLACK, "", sourceElement.getX() + 1, sourceElement.getY());
        this.leftElement = new Element(Color.BLACK, "", sourceElement.getX() - 1, sourceElement.getY());
        this.downElement = new Element(Color.BLACK, "", sourceElement.getX(), sourceElement.getY() + 1);
        this.upElement = new Element(Color.BLACK, "", sourceElement.getX(), sourceElement.getY() - 1);

    }

    public List<Element> asList() {
        List<Element> elementList = new ArrayList<Element>();
        elementList.add(rightElement);
        elementList.add(leftElement);
        elementList.add(downElement);
        elementList.add(upElement);

        return elementList;
    }
}
