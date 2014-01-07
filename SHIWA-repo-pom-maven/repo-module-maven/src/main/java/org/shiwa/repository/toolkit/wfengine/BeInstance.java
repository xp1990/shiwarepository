/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author xp
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "idBackend", discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "be_instance")
@XmlRootElement
@XmlSeeAlso({
    GLite.class,
    GT2.class,
    GT4.class,
    Local.class
})
@NamedQueries({
    @NamedQuery(name = "BeInstance.findAll", query = "SELECT b FROM BeInstance b"),
    @NamedQuery(name = "BeInstance.findByIdBackendInst", query = "SELECT b FROM BeInstance b WHERE b.idBackendInst = :idBackendInst"),
    @NamedQuery(name = "BeInstance.findByBackendInstName", query = "SELECT b FROM BeInstance b WHERE b.backendInstName = :backendInstName"),
    @NamedQuery(name = "BeInstance.findByBackendOutput", query = "SELECT b FROM BeInstance b WHERE b.backendOutput = :backendOutput"),
    @NamedQuery(name = "BeInstance.findByBackendErrorOut", query = "SELECT b FROM BeInstance b WHERE b.backendErrorOut = :backendErrorOut")})
public abstract class BeInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idBackendInst")
    private Integer idBackendInst;
    @Size(max = 50)
    @Column(name = "backendInstName")
    private String backendInstName;
    @Size(max = 200)
    @Column(name = "backendOutput")
    private String backendOutput;
    @Size(max = 200)
    @Column(name = "backendErrorOut")
    private String backendErrorOut;
    @Size(max = 100)
    @Column(name = "resource")
    private String resource;
    @JoinColumn(name = "idBackend", referencedColumnName = "idBackend")
    @ManyToOne(optional = false)
    private Backend idBackend;
    @JoinColumn(name = "idOS", referencedColumnName = "idOS")
    @ManyToOne(optional = false)
    private OperatingSystems idOS;
    @OneToMany(mappedBy = "idBackendInst")
    private Collection<WEImplementation> weImplementationCollection;
    @JoinColumn(name = "we_dev", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User WEDev;

    public BeInstance() {
    }

    public BeInstance(String _name, Backend _idBackend, String _backendOut, String _backendErr, OperatingSystems _idOS, User _wedev) {

        this.backendInstName = _name;
        this.idBackend = _idBackend;
        this.backendOutput = _backendOut;
        this.backendErrorOut = _backendErr;
        this.idOS = _idOS;
        this.WEDev = _wedev;
    }

    public BeInstance(String _name, Backend _idBackend, String _backendOut, String _backendErr, OperatingSystems _idOS, String _resource, User _wedev) {

        this.backendInstName = _name;
        this.idBackend = _idBackend;
        this.backendOutput = _backendOut;
        this.backendErrorOut = _backendErr;
        this.idOS = _idOS;
        this.resource = _resource;
        this.WEDev = _wedev;
    }

    /*
     * emk
     * Necessary?!
    public static BeInstance BeInstanceFactory(String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, String _jobManager, JobType _jobType, OperatingSystems _idOS)
    {
    return new BeInstance(_name, _idBackend, _site, _backendOut, _backendErr, _jobManager, _jobType, _idOS);
    }
    public static BeInstance BeInstanceFactory(String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, String _jobManager, JobType _jobType, OperatingSystems _idOS, int _maxPar)
    {
    return new BeInstance(_name, _idBackend, _site, _backendOut, _backendErr, _jobManager, _jobType, _idOS, _maxPar);
    }
     */
    public User getWEDev() {
        return WEDev;
    }

    public void setWEDev(User WEDev) {
        this.WEDev = WEDev;
    }

    public BeInstance(Integer idBackendInst) {
        this.idBackendInst = idBackendInst;
    }

    public Integer getIdBackendInst() {
        return idBackendInst;
    }

    public void setIdBackendInst(Integer idBackendInst) {
        this.idBackendInst = idBackendInst;
    }

    public String getBackendInstName() {
        return backendInstName;
    }

    public void setBackendInstName(String backendInstName) {
        this.backendInstName = backendInstName;
    }

    public String getBackendOutput() {
        return backendOutput;
    }

    public void setBackendOutput(String backendOutput) {
        this.backendOutput = backendOutput;
    }

    public String getBackendErrorOut() {
        return backendErrorOut;
    }

    public void setBackendErrorOut(String backendErrorOut) {
        this.backendErrorOut = backendErrorOut;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Backend getIdBackend() {
        return idBackend;
    }

    public void setIdBackend(Backend idBackend) {
        this.idBackend = idBackend;
    }

    public OperatingSystems getIdOS() {
        return idOS;
    }

    public void setIdOS(OperatingSystems idOS) {
        this.idOS = idOS;
    }

    @XmlTransient
    public Collection<WEImplementation> getWeImplementationCollection() {
        return weImplementationCollection;
    }

    public void setWeImplementationCollection(Collection<WEImplementation> weImplementationCollection) {
        this.weImplementationCollection = weImplementationCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idBackendInst != null ? idBackendInst.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BeInstance)) {
            return false;
        }
        BeInstance other = (BeInstance) object;
        if ((this.idBackendInst == null && other.idBackendInst != null) || (this.idBackendInst != null && !this.idBackendInst.equals(other.idBackendInst))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.BeInstance[ idBackendInst=" + idBackendInst + " ]";
    }
}
