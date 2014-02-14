/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import uk.ac.wmin.edgi.repository.entities.Platform;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author xp
 */
@Entity
@Table(name = "we_implementation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WEImplementation.listByName", query = "SELECT wi FROM WEImplementation wi"),
    @NamedQuery(name = "WEImplementation.findByName", query = "SELECT wi FROM WEImplementation wi WHERE wi.nameWEImp = :name"),
    @NamedQuery(name = "WEImplementation.listAccordingWE", query = "SELECT wi FROM WEImplementation wi WHERE wi.idWE = :idWE"),
    @NamedQuery(name = "WEImplementation.findAll", query = "SELECT w FROM WEImplementation w"),
    @NamedQuery(name = "WEImplementation.findByShellFile", query = "SELECT w FROM WEImplementation w WHERE w.shellWEFileId = :shellFile"),
    @NamedQuery(name = "WEImplementation.findByZipFile", query = "SELECT w FROM WEImplementation w WHERE w.zipWEFileId = :zipFile"),
    @NamedQuery(name = "WEImplementation.findByIdWEImp", query = "SELECT w FROM WEImplementation w WHERE w.idWEImp = :idWEImp"),
    @NamedQuery(name = "WEImplementation.findByNameWEImp", query = "SELECT w FROM WEImplementation w WHERE w.nameWEImp = :nameWEImp"),
    @NamedQuery(name = "WEImplementation.findByBEI", query = "SELECT w FROM WEImplementation w WHERE w.idBackendInst = :bei"),
    @NamedQuery(name = "WEImplementation.findByNameWEImpAndIdWE", query = "SELECT w FROM WEImplementation w WHERE w.nameWEImp = :nameWEImp and w.idWE = :idWE"),
    @NamedQuery(name = "WEImplementation.findByDescriptionWEImp", query = "SELECT w FROM WEImplementation w WHERE w.descriptionWEImp = :descriptionWEImp"),
    @NamedQuery(name = "WEImplementation.findByPreDeployed", query = "SELECT w FROM WEImplementation w WHERE w.preDeployed = :preDeployed"),
    @NamedQuery(name = "WEImplementation.findByPrefixData", query = "SELECT w FROM WEImplementation w WHERE w.prefixData = :prefixData")})
public class WEImplementation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idWEImp")
    private Integer idWEImp;
    @Size(max = 50)
    @Column(name = "nameWEImp")
    private String nameWEImp;
    @Size(max = 5000)
    @Column(name = "descriptionWEImp")
    private String descriptionWEImp;
    @Column(name = "preDeployed")
    private Boolean preDeployed;
    @Size(max = 500)
    @Column(name = "prefixData")
    private String prefixData;
    @JoinColumn(name = "zipWEFileId", referencedColumnName = "idWEFile")
    @ManyToOne
    private WEUploadedFile zipWEFileId;
    @JoinColumn(name = "shellWEFileId", referencedColumnName = "idWEFile")
    @ManyToOne
    private WEUploadedFile shellWEFileId;
    @Column(name = "shellPath")
    private String shellPath;
    @JoinColumn(name = "idBackendInst", referencedColumnName = "idBackendInst")
    @ManyToOne(optional = false)
    private BeInstance idBackendInst;
    @JoinColumn(name = "idWE", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Platform idWE;
    @JoinColumn(name = "we_dev", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User WEDev;
    @Column(name = "enabled") private boolean enabled;

    public WEImplementation() {
    }

    public WEImplementation
        ( String _name, String _description, boolean _preDeployed, String _prefixData, Platform _we, BeInstance _be, String _shell, User _WEDev)
    {
	this.nameWEImp = _name;
        this.descriptionWEImp = _description;
        this.preDeployed = _preDeployed;
        this.prefixData = _prefixData;
        this.idBackendInst = _be;
        this.idWE = _we;
        this.shellPath = _shell;
        this.WEDev = _WEDev;
        this.enabled = false;
    }

       public WEImplementation
        ( String _name, String _description, boolean _preDeployed, String _prefixData, Platform _we, BeInstance _be, WEUploadedFile _zip, WEUploadedFile _shellFile, User _WEDev)
    {
	this.nameWEImp = _name;
        this.descriptionWEImp = _description;
        this.preDeployed = _preDeployed;
        this.prefixData = _prefixData;
        this.idBackendInst = _be;
        this.idWE = _we;
        this.shellWEFileId = _shellFile;
        this.zipWEFileId = _zip;
        this.WEDev = _WEDev;
        this.enabled = false;
    }

    public static WEImplementation WEImplementationFactory
	( String _name, String _description, boolean _preDeployed, String _prefixData, Platform _we, BeInstance _be, String _shell, User _WEDev)
	throws WEBuildingException
    {
	if ( _name.length() < 3 || _name.length() > 50)
	    throw new WEBuildingException( "name", _name );
        if ( _description.length() > 5000 )
            throw new WEBuildingException( "description", _description );
        if ( _prefixData.length() > 500)
	    throw new WEBuildingException( "extraArg", _prefixData );

	return new WEImplementation( _name, _description, _preDeployed, _prefixData, _we, _be, _shell, _WEDev);
    }

    public static WEImplementation WEImplementationFactory
	( String _name, String _description, boolean _preDeployed, String _prefixData, Platform _we, BeInstance _be, WEUploadedFile _zip, WEUploadedFile _shell, User _WEDev)
	throws WEBuildingException
    {
	if ( _name.length() < 3 || _name.length() > 50)
	    throw new WEBuildingException( "name", _name );
        if ( _description.length() > 5000 )
            throw new WEBuildingException( "description", _description );
        if ( _prefixData.length() > 500)
	    throw new WEBuildingException( "extraArg", _prefixData );

	return new WEImplementation( _name, _description, _preDeployed, _prefixData, _we, _be, _zip, _shell, _WEDev);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public WEImplementation(Integer idWEImp) {
        this.idWEImp = idWEImp;
    }

    public Integer getIdWEImp() {
        return idWEImp;
    }

    public void setIdWEImp(Integer idWEImp) {
        this.idWEImp = idWEImp;
    }

    public String getNameWEImp() {
        return nameWEImp;
    }

        public WEUploadedFile getShellWEFileId() {
        return shellWEFileId;
    }

    public void setShellWEFileId(WEUploadedFile shellWEFileId) {
        this.shellWEFileId = shellWEFileId;
    }

    public void setNameWEImp(String nameWEImp) {
        this.nameWEImp = nameWEImp;
    }

    public String getDescriptionWEImp() {
        return descriptionWEImp;
    }

    public void setDescriptionWEImp(String descriptionWEImp) {
        this.descriptionWEImp = descriptionWEImp;
    }

    public Boolean getPreDeployed() {
        return preDeployed;
    }

    public void setPreDeployed(Boolean preDeployed) {
        this.preDeployed = preDeployed;
    }

    public String getPrefixData() {
        return prefixData;
    }

    public void setPrefixData(String prefixData) {
        this.prefixData = prefixData;
    }

    public WEUploadedFile getZipWEFileId() {
        return zipWEFileId;
    }

    public void setZipWEFileId(WEUploadedFile zipWEFileId) {
        this.zipWEFileId = zipWEFileId;
    }

    public String getShellPath() {
        return shellPath;
    }

    public void setShellPath(String shellPath) {
        this.shellPath = shellPath;
    }

    public BeInstance getIdBackendInst() {
        return idBackendInst;
    }

    public void setIdBackendInst(BeInstance idBackendInst) {
        this.idBackendInst = idBackendInst;
    }

    public Platform getIdWE() {
        return idWE;
    }

    public void setIdWE(Platform idWE) {
        this.idWE = idWE;
    }

    public User getWEDev() {
        return WEDev;
    }

    public void setWEDev(User WEDev) {
        this.WEDev = WEDev;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idWEImp != null ? idWEImp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WEImplementation)) {
            return false;
        }
        WEImplementation other = (WEImplementation) object;
        if ((this.idWEImp == null && other.idWEImp != null) || (this.idWEImp != null && !this.idWEImp.equals(other.idWEImp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.WeImplementation[ idWEImp=" + idWEImp + " ]";
    }

}
