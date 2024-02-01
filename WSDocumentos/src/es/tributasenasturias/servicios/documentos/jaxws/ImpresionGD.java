
package es.tributasenasturias.servicios.documentos.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "impresionGD", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "impresionGD", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "origenDatos",
    "codigoVerificacion",
    "altaDocumento"
})
public class ImpresionGD {

    @XmlElement(name = "origenDatos", namespace = "")
    private String origenDatos;
    @XmlElement(name = "codigoVerificacion", namespace = "")
    private String codigoVerificacion;
    @XmlElement(name = "altaDocumento", namespace = "")
    private boolean altaDocumento;

    /**
     * 
     * @return
     *     returns String
     */
    public String getOrigenDatos() {
        return this.origenDatos;
    }

    /**
     * 
     * @param origenDatos
     *     the value for the origenDatos property
     */
    public void setOrigenDatos(String origenDatos) {
        this.origenDatos = origenDatos;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getCodigoVerificacion() {
        return this.codigoVerificacion;
    }

    /**
     * 
     * @param codigoVerificacion
     *     the value for the codigoVerificacion property
     */
    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isAltaDocumento() {
        return this.altaDocumento;
    }

    /**
     * 
     * @param altaDocumento
     *     the value for the altaDocumento property
     */
    public void setAltaDocumento(boolean altaDocumento) {
        this.altaDocumento = altaDocumento;
    }

}
