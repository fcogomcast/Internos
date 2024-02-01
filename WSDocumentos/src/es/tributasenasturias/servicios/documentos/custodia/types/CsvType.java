package es.tributasenasturias.servicios.documentos.custodia.types;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "csvType", propOrder = {
    "firmaCSV",
    "firmante"
})
public class CsvType {

    @XmlElement(name = "FirmaCSV", required=true)
    protected String firmaCSV;
    @XmlElement(name = "Firmante")
    protected String firmante;

    /**
     * Gets the value of the firmaCSV property.
     * 
     */
    public String getFirmaCSV() {
        return firmaCSV;
    }

    /**
     * Sets the value of the firmaCSV property.
     * 
     */
    public void setFirmaCSV(String value) {
        this.firmaCSV = value;
    }

    /**
     * Gets the value of the firmante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmante() {
        return firmante;
    }

    /**
     * Sets the value of the firmante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmante(String value) {
        this.firmante = value;
    }

}
