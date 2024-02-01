package es.tributasenasturias.servicios.documentos.custodia.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custodiarComposicion", propOrder = {
    "gzippedData",
    "codigoUsuario",
    "tipoElemento",
    "idElementoTributas",
    "idArchivoPadre",
    "firma"
})
@XmlRootElement(name="custodiarComposicion")
public class CustodiarComposicion {
	@XmlElement(name="gzippedData", required=true)
	protected String gzippedData;
	@XmlElement(name="codigoUsuario", required=true)
	protected String codigoUsuario;
	@XmlElement(name="tipoElemento", required=true)
	protected String tipoElemento;
	@XmlElement(name="idElementoTributas", required=false)
	protected String idElementoTributas;
	@XmlElement(required=false,nillable=true)
    protected String idArchivoPadre;
	@XmlElement(name="firma", required=false)
	protected FirmaType firma;
	public String getGzippedData() {
		return gzippedData;
	}
	public void setGzippedData(String gzippedData) {
		this.gzippedData = gzippedData;
	}
	public String getCodigoUsuario() {
		return codigoUsuario;
	}
	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}
	public String getTipoElemento() {
		return tipoElemento;
	}
	public void setTipoElemento(String tipoElemento) {
		this.tipoElemento = tipoElemento;
	}
	public String getIdElementoTributas() {
		return idElementoTributas;
	}
	public void setIdElementoTributas(String idElementoTributas) {
		this.idElementoTributas = idElementoTributas;
	}
	public FirmaType getFirma() {
		return firma;
	}
	public void setFirma(FirmaType firma) {
		this.firma = firma;
	}
	
	
}
