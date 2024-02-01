
package es.tributasenasturias.servicios.documentos.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "altaReimprimibleResponse", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaReimprimibleResponse", namespace = "http://documentos.servicios.tributasenasturias.es/")
public class AltaReimprimibleResponse {

    @XmlElement(name = "documento", namespace = "")
    private String documento;

    /**
     * 
     * @return
     *     returns String
     */
    public String getDocumento() {
        return this.documento;
    }

    /**
     * 
     * @param documento
     *     the value for the documento property
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

}
