
package es.tributasenasturias.servicios.documentos.custodia.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "custodiarComposicion", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custodiarComposicion", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "gzippedData",
    "codigoUsuario",
    "tipoElemento",
    "idElementoTributas",
    "idArchivoPadre",
    "firma"
})
public class CustodiarPDFConstruido {

    @XmlElement(name = "gzippedData", namespace = "")
    private String gzippedData;
    @XmlElement(name = "codigoUsuario", namespace = "")
    private String codigoUsuario;
    @XmlElement(name = "tipoElemento", namespace = "")
    private String tipoElemento;
    @XmlElement(name = "idElementoTributas", namespace = "")
    private String idElementoTributas;
    @XmlElement(name = "idArchivoPadre", namespace = "")
    private String idArchivoPadre;
    @XmlElement(name = "firma", namespace = "")
    private FirmaType firma;

    /**
     * 
     * @return
     *     returns String
     */
    public String getGzippedData() {
        return this.gzippedData;
    }

    /**
     * 
     * @param gzippedData
     *     the value for the gzippedData property
     */
    public void setGzippedData(String gzippedData) {
        this.gzippedData = gzippedData;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getCodigoUsuario() {
        return this.codigoUsuario;
    }

    /**
     * 
     * @param codigoUsuario
     *     the value for the codigoUsuario property
     */
    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getTipoElemento() {
        return this.tipoElemento;
    }

    /**
     * 
     * @param tipoElemento
     *     the value for the tipoElemento property
     */
    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdElementoTributas() {
        return this.idElementoTributas;
    }

    /**
     * 
     * @param idElementoTributas
     *     the value for the idElementoTributas property
     */
    public void setIdElementoTributas(String idElementoTributas) {
        this.idElementoTributas = idElementoTributas;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdArchivoPadre() {
        return this.idArchivoPadre;
    }

    /**
     * 
     * @param idArchivoPadre
     *     the value for the idArchivoPadre property
     */
    public void setIdArchivoPadre(String idArchivoPadre) {
        this.idArchivoPadre = idArchivoPadre;
    }

    /**
     * 
     * @return
     *     returns FirmaType
     */
    public FirmaType getFirma() {
        return this.firma;
    }

    /**
     * 
     * @param firma
     *     the value for the firma property
     */
    public void setFirma(FirmaType firma) {
        this.firma = firma;
    }

}
