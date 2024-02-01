package es.tributasenasturias.servicios.documentos.custodia.types;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "firmaType", propOrder = {
    "csv",
    "certificado"
})
public class FirmaType {

    @XmlElement(name = "CSV", required = false)
    protected CsvType csv;
    @XmlElement(name = "Certificado", required = false)
    protected CertificadoType certificado;

    /**
     * Gets the value of the csv property.
     * 
     * @return
     *     possible object is
     *     {@link CsvType }
     *     
     */
    public CsvType getCSV() {
        return csv;
    }

    /**
     * Sets the value of the csv property.
     * 
     * @param value
     *     allowed object is
     *     {@link CsvType }
     *     
     */
    public void setCSV(CsvType value) {
        this.csv = value;
    }

    /**
     * Gets the value of the certificado property.
     * 
     * @return
     *     possible object is
     *     {@link CertificadoType }
     *     
     */
    public CertificadoType getCertificado() {
        return certificado;
    }

    /**
     * Sets the value of the certificado property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificadoType }
     *     
     */
    public void setCertificado(CertificadoType value) {
        this.certificado = value;
    }

}
