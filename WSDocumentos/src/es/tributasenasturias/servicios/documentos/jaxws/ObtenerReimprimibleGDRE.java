
package es.tributasenasturias.servicios.documentos.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerReimprimibleGDRE", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerReimprimibleGDRE", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "idGdre",
    "codigoVerificacion"
})
public class ObtenerReimprimibleGDRE {

    @XmlElement(name = "id_gdre", namespace = "")
    private String idGdre;
    @XmlElement(name = "codigoVerificacion", namespace = "")
    private String codigoVerificacion;

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdGdre() {
        return this.idGdre;
    }

    /**
     * 
     * @param idGdre
     *     the value for the idGdre property
     */
    public void setIdGdre(String idGdre) {
        this.idGdre = idGdre;
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
