/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import uk.ac.wmin.edgi.repository.common.ImplementationStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

/**
 *
 * @author zsolt
 */
@Entity
@Table(name = "implementation")
@NamedQueries({
    @NamedQuery(name = "Implementation.loadImplementationFull", query = "SELECT i FROM Implementation i WHERE i.id=:id AND i.submittable=TRUE"),
    @NamedQuery(name = "Implementation.getValidatedImpIDByAppNameAndVersion", query = "SELECT i.id FROM Implementation i, Application a WHERE a.name=:appName AND a.id=i.application.id AND i.version=:version AND i.submittable=TRUE"),
    @NamedQuery(name = "Implementation.lastUpdated", query = "SELECT MAX(i.updated) FROM Implementation i"),
    @NamedQuery(name = "Implementation.findByAppIDAndVersionAndPlatformID", query = "SELECT i FROM Implementation i WHERE i.application.id = :appId AND i.version = :version AND i.platform.id = :platId"),
    @NamedQuery(name = "Implementation.listByAppIDs", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpListItemTO(i.id, i.application.id) FROM Implementation i WHERE i.application.id IN :appIDs"),
    @NamedQuery(name = "Implementation.listByAppIDsAttr", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpListItemTO(t.implementation.id, t.implementation.application.id) FROM ImpAttribute t WHERE t.implementation.application.id = :appID AND t.name = :attrName AND t.value LIKE :attrValue"),
    @NamedQuery(name = "Implementation.loadImplementation", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE i.id = :impId"),
    @NamedQuery(name = "Implementation.loadImplementationsOfApplication", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable,i.views) FROM Implementation i WHERE i.application.id = :appId ORDER BY i.status desc, i.updated desc"),
    @NamedQuery(name = "Implementation.loadImplementationsOfPlatform", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE i.platform.id = :platId"),
    @NamedQuery(name = "Implementation.loadImplementations", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = i.application.group.id AND i.application.groupRead = TRUE)  OR i.application.published = TRUE OR i.application.owner.id = :userId OR i.application.othersRead = TRUE ORDER BY i.status desc, i.updated desc" ),
    @NamedQuery(name = "Implementation.loadAllImplementations", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i ORDER BY i.status desc, i.updated desc"),
    @NamedQuery(name = "Implementation.loadFilteredImplementations", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE EXISTS (SELECT a FROM ImpAttribute a WHERE a.implementation = i AND (a.name LIKE :impAttrNameFilter AND a.value LIKE :impAttrValueFilter)) AND (EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = i.application.group.id AND i.application.groupRead = TRUE) OR i.application.published = TRUE OR i.application.owner.id = :userId OR i.application.othersRead = TRUE)"),
    @NamedQuery(name = "Implementation.loadPublishedImplementations", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE i.application.published = TRUE"),
    @NamedQuery(name = "Implementation.loadFilteredPublishedImplementations", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE EXISTS (SELECT a FROM ImpAttribute a WHERE a.implementation = i AND (a.name LIKE :impAttrNameFilter AND a.value LIKE :impAttrValueFilter)) AND i.application.published = TRUE"),
    @NamedQuery(name = "Implementation.loadImplementationsOfApplicationUserCanRead", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE (EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = i.application.group.id AND i.application.groupRead = TRUE)  OR i.application.published = TRUE OR i.application.owner.id = :userId OR i.application.othersRead = TRUE) AND i.application.id = :appId ORDER BY i.status desc, i.updated desc"),
    @NamedQuery(name = "Implementation.loadImplementationsOfApplicationReadableByOthers", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE i.application.othersRead = TRUE AND i.application.id = :appId ORDER BY i.status desc, i.updated desc"),
    @NamedQuery(name = "Implementation.loadImplementationsReadableByOthers", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(i.id, i.application.id, i.application.name, i.platform.name, i.platform.version, i.version, i.status, i.created, i.updated, i.submittable, i.views) FROM Implementation i WHERE i.application.othersRead = TRUE ORDER BY i.status desc, i.updated desc")
})
@TableGenerator(name="imp_gen", table="generator", pkColumnName="name", pkColumnValue="imp_gen", valueColumnName="value")
public class Implementation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="imp_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "version") private String version;
    @Enumerated(EnumType.STRING) @Column(name = "status") private ImplementationStatus status;
    @JoinColumn(name = "plat_id", referencedColumnName = "id") @ManyToOne(optional = false) private Platform platform;
    @JoinColumn(name = "app_id", referencedColumnName = "id") @ManyToOne(optional = false) private Application application;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "implementation") @MapKey(name="name") private Map<String, ImpAttribute> attributes;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "implementation") @OrderBy("created DESC") private List<ImpComment> comments;
    @Temporal(javax.persistence.TemporalType.DATE) @Column(name = "created") private Date created;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP) @Column(name = "updated") private Date updated;
    @Column(name = "submittable") private boolean submittable;
    @Basic(optional = true) @Column(name = "views") private int views;

    public Implementation() {
        this.views = 0;
    }

    public Implementation(String version, Platform platform, Application application) {
        this.version = version;
        this.status = status.NEW;
        this.platform = platform;
        this.application = application;
        this.views = 0;
    }

    public Implementation(String version, Platform platform, Application application, Date created, Date updated) {
        this.version = version;
        this.status = status.NEW;
        this.platform = platform;
        this.application = application;
        this.created = created;
        this.updated = updated;
        this.views = 0;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void incViews(){
        views++;
    }

    public boolean isSubmittable() {
        return submittable;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Map<String, ImpAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, ImpAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<ImpComment> getComments() {
        return comments;
    }

    public void setComments(List<ImpComment> comments) {
        this.comments = comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public ImplementationStatus getStatus() {
        return status;
    }

    public void setStatus(ImplementationStatus status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Implementation)) {
            return false;
        }
        Implementation other = (Implementation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.Implementation[id=" + id + "]";
    }

}
