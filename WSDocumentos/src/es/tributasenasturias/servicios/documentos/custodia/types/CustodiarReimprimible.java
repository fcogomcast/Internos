package es.tributasenasturias.servicios.documentos.custodia.types;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custodiarReimprimible", propOrder = {
    "idGdre",
    "codigoUsuario",
    "idArchivoPadre",
    "firma"
})
@XmlRootElement(name = "custodiarReimprimible")
public class CustodiarReimprimible {

	@XmlElement(name = "idGdre", required = true)
    protected String idGdre;
    @XmlElement(required = true)
    protected String codigoUsuario;
    @XmlElement(required=false,nillable=true)
    protected String idArchivoPadre;
    @XmlElement(required=false)
    protected FirmaType firma;

    /**
     * Gets the value of the idGdre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdGdre() {
        return idGdre;
    }

    /**
     * Sets the value of the idGdre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdGdre(String value) {
        this.idGdre = value;
    }

    /**
     * Gets the value of the codigoUsuario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    /**
     * Sets the value of the codigoUsuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoUsuario(String value) {
        this.codigoUsuario = value;
    }

    /**
     * Gets the value of the firma property.
     * 
     * @return
     *     possible object is
     *     {@link FirmaType }
     *     
     */
    public FirmaType getFirma() {
        return firma;
    }
    
    /**
     * Sets the value of the firma property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmaType }
     *     
     */
    public void setFirma(FirmaType value) {
        this.firma = value;
    }

}
