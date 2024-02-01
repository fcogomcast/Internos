
package es.tributasenasturias.servicios.documentos.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "altaReimprimible", namespace = "http://documentos.servicios.tributasenasturias.es/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaReimprimible", namespace = "http://documentos.servicios.tributasenasturias.es/", propOrder = {
    "nombre",
    "tipo",
    "codigoVerificacion",
    "nifSujetoPasivo",
    "nifPresentador",
    "tipoDocumento",
    "idSesion",
    "comprimirDocumento",
    "elemento",
    "tipoElemento",
    "comprimirSalida"
})
public class AltaReimprimible {

    @XmlElement(name = "nombre", namespace = "")
    private String nombre;
    @XmlElement(name = "tipo", namespace = "")
    private String tipo;
    @XmlElement(name = "codigo_verificacion", namespace = "")
    private String codigoVerificacion;
    @XmlElement(name = "nif_sujeto_pasivo", namespace = "")
    private String nifSujetoPasivo;
    @XmlElement(name = "nif_presentador", namespace = "")
    private String nifPresentador;
    @XmlElement(name = "tipo_documento", namespace = "")
    private String tipoDocumento;
    @XmlElement(name = "id_sesion", namespace = "")
    private String idSesion;
    @XmlElement(name = "comprimir_documento", namespace = "")
    private boolean comprimirDocumento;
    @XmlElement(name = "elemento", namespace = "")
    private String elemento;
    @XmlElement(name = "tipo_elemento", namespace = "")
    private String tipoElemento;
    @XmlElement(name = "comprimir_salida", namespace = "")
    private boolean comprimirSalida;

    /**
     * 
     * @return
     *     returns String
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * 
     * @param nombre
     *     the value for the nombre property
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    /**
     * 
     * @return
     *     returns String
     */
    public String getNifSujetoPasivo() {
        return this.nifSujetoPasivo;
    }

    /**
     * 
     * @param nifSujetoPasivo
     *     the value for the nifSujetoPasivo property
     */
    public void setNifSujetoPasivo(String nifSujetoPasivo) {
        this.nifSujetoPasivo = nifSujetoPasivo;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getNifPresentador() {
        return this.nifPresentador;
    }

    /**
     * 
     * @param nifPresentador
     *     the value for the nifPresentador property
     */
    public void setNifPresentador(String nifPresentador) {
        this.nifPresentador = nifPresentador;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getTipoDocumento() {
        return this.tipoDocumento;
    }

    /**
     * 
     * @param tipoDocumento
     *     the value for the tipoDocumento property
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdSesion() {
        return this.idSesion;
    }

    /**
     * 
     * @param idSesion
     *     the value for the idSesion property
     */
    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isComprimirDocumento() {
        return this.comprimirDocumento;
    }

    /**
     * 
     * @param comprimirDocumento
     *     the value for the comprimirDocumento property
     */
    public void setComprimirDocumento(boolean comprimirDocumento) {
        this.comprimirDocumento = comprimirDocumento;
    }

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
     *     returns boolean
     */
    public boolean isComprimirSalida() {
        return this.comprimirSalida;
    }

    /**
     * 
     * @param comprimirSalida
     *     the value for the comprimirSalida property
     */
    public void setComprimirSalida(boolean comprimirSalida) {
        this.comprimirSalida = comprimirSalida;
    }

}
