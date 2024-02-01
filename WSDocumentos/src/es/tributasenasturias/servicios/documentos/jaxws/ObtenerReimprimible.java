
package es.tributasenasturias.servicios.documentos.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerReimprimible", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerReimprimible", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "elemento",
    "tipo",
    "codigoVerificacion"
})
public class ObtenerReimprimible {

    @XmlElement(name = "elemento", namespace = "")
    private String elemento;
    @XmlElement(name = "tipo", namespace = "")
    private String tipo;
    @XmlElement(name = "codigoVerificacion", namespace = "")
    private String codigoVerificacion;

    /**
     * 
     * @return
     *     returns String
     */
    public String getElemento() {
        return this.elemento;
    }

    /**
     * 
     * @param elemento
     *     the value for the elemento property
     */
    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * 
     * @param tipo
     *     the value for the tipo property
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
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

}
