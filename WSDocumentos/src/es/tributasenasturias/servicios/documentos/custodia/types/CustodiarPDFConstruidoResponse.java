
package es.tributasenasturias.servicios.documentos.custodia.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "custodiarComposicionResponse", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custodiarComposicionResponse", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "esError",
    "mensaje",
    "idadar",
    "csv",
    "hash"
})
public class CustodiarPDFConstruidoResponse {

    @XmlElement(name = "esError", namespace = "")
    private Boolean esError;
    @XmlElement(name = "mensaje", namespace = "")
    private String mensaje;
    @XmlElement(name = "idadar", namespace = "")
    private String idadar;
    @XmlElement(name = "csv", namespace = "")
    private String csv;
    @XmlElement(name = "hash", namespace = "")
    private String hash;

    /**
     * 
     * @return
     *     returns Boolean
     */
    public Boolean getEsError() {
        return this.esError;
    }

    /**
     * 
     * @param esError
     *     the value for the esError property
     */
    public void setEsError(Boolean esError) {
        this.esError = esError;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getMensaje() {
        return this.mensaje;
    }

    /**
     * 
     * @param mensaje
     *     the value for the mensaje property
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdadar() {
        return this.idadar;
    }

    /**
     * 
     * @param idadar
     *     the value for the idadar property
     */
    public void setIdadar(String idadar) {
        this.idadar = idadar;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getCsv() {
        return this.csv;
    }

    /**
     * 
     * @param csv
     *     the value for the csv property
     */
    public void setCsv(String csv) {
        this.csv = csv;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * 
     * @param hash
     *     the value for the hash property
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

}
