package pl.poznan.put.helper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RGB extends XMLSerializable {
    private static final long serialVersionUID = 9081446619961449105L;

    public static RGB newInstance(float[] rgb) {
        RGB instance = new RGB();
        instance.r = rgb[0];
        instance.g = rgb[1];
        instance.b = rgb[2];
        return instance;
    }

    private float b;
    private float g;
    private float r;

    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }

    public float getR() {
        return r;
    }

    @XmlElement
    public void setB(float b) {
        this.b = b;
    }

    @XmlElement
    public void setG(float g) {
        this.g = g;
    }

    @XmlElement
    public void setR(float r) {
        this.r = r;
    }

}
