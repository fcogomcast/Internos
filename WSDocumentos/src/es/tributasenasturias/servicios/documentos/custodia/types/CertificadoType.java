package es.tributasenasturias.servicios.documentos.custodia.types;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certificadoType", propOrder = {
    "firmaCertificado",
    "ltv"
})
public class CertificadoType {

    @XmlElement(name = "FirmaCertificado", required=true)
    protected String firmaCertificado;
    @XmlElement(name = "LTV")
    protected String ltv;

    /**
     * Gets the value of the firmaCertificado property.
     * 
     */
    public String getFirmaCertificado() {
        return firmaCertificado;
    }

    /**
     * Sets the value of the firmaCertificado property.
     * 
     */
    public void setFirmaCertificado(String value) {
        this.firmaCertificado = value;
    }

    /**
     * Gets the value of the ltv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLTV() {
        return ltv;
    }

    /**
     * Sets the value of the ltv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLTV(String value) {
        this.ltv = value;
    }
}
