/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "application")
@NamedQueries({
    @NamedQuery(name = "Application.lastUpdated", query = "SELECT MAX(a.updated) FROM Application a"),
    @NamedQuery(name = "Application.findByName", query = "SELECT a FROM Application a WHERE a.name = :name"),
    @NamedQuery(name = "Application.listAll", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AppListItemTO(a.id, a.name) FROM Application a"),
    @NamedQuery(name = "Application.listByImpAttr", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AppListItemTO(a.id, a.name) FROM Application a WHERE EXISTS (SELECT t FROM ImpAttribute t WHERE t.implementation.application = a AND t.name = :impAttrName AND t.value LIKE :impAttrValue)"),
    @NamedQuery(name = "Application.loadApplication", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.id = :appId"),
    @NamedQuery(name = "Application.loadAllApplications", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a order by a.updated  desc"),
    @NamedQuery(name = "Application.loadFilteredApplications", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE EXISTS (SELECT t FROM AppAttribute t WHERE t.application = a AND (t.name LIKE :appAttrNameFilter AND t.value LIKE :appAttrValueFilter)) AND (EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = a.group.id AND a.groupRead = TRUE) OR a.published = TRUE OR a.owner.id = :userId OR a.othersRead = TRUE)"),
    @NamedQuery(name = "Application.loadFilteredPublishedApplications", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE EXISTS (SELECT t FROM AppAttribute t WHERE t.application = a AND (t.name LIKE :appAttrNameFilter AND t.value LIKE :appAttrValueFilter)) AND a.published = TRUE"),
    @NamedQuery(name = "Application.loadApplicationsOfUser", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.owner.id = :ownerId"),
    @NamedQuery(name = "Application.loadApplicationsOfGroup", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.group.id = :groupId"),
    @NamedQuery(name = "Application.loadAppsUserCanRead", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE (EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = a.group.id AND a.groupRead = TRUE) OR a.published = TRUE OR a.owner.id = :userId OR a.othersRead = TRUE) order by a.updated desc"),
    @NamedQuery(name = "Application.loadAppsUserCanModify", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE (EXISTS (SELECT g FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.id = a.group.id AND a.groupModify = TRUE) OR a.owner.id = :userId)"),
    @NamedQuery(name = "Application.loadNonPublishedApplications", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.published = FALSE"),
    @NamedQuery(name = "Application.loadApplicationsReadyForValidation", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.published = FALSE AND a.othersRead = TRUE"),
    @NamedQuery(name = "Application.loadAppsReadableByOthers", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.othersRead = TRUE order by a.updated desc"),
    @NamedQuery(name = "Application.loadPublishedApplications", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(a.id, a.name, a.owner.loginName, a.group.name, a.description, a.groupRead, a.othersRead, a.groupDownload, a.othersDownload, a.groupModify, a.published, a.created, a.updated, a.views) FROM Application a WHERE a.published = TRUE order by a.updated desc")
})
@TableGenerator(name="app_gen", table="generator", pkColumnName="name", pkColumnValue="app_gen", valueColumnName="value")
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="app_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "name") private String name;
    @Basic(optional = false) @Column(name = "group_read") private Boolean groupRead;
    @Basic(optional = false) @Column(name = "others_read") private Boolean othersRead;
    @Basic(optional = false) @Column(name = "group_download") private Boolean groupDownload;
    @Basic(optional = false) @Column(name = "others_download") private Boolean othersDownload;
    @Basic(optional = false) @Column(name = "group_modify") private Boolean groupModify;
    @Basic(optional = false) @Column(name = "published") private Boolean published;
    @Basic(optional = false) @Column(name = "description") private String description;
    @Temporal(javax.persistence.TemporalType.DATE) @Column(name = "created") private Date created;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP) @Column(name = "updated") private Date updated;
    @JoinColumn(name = "owner_id", referencedColumnName = "id") @ManyToOne(optional = false) private User owner;
    @JoinColumn(name = "group_id", referencedColumnName = "id") @ManyToOne(optional = false) private UserGroup group;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application") private Collection<Implementation> implementations;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application") @MapKey(name="name")  private Map<String, AppAttribute> attributes;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application") @OrderBy("created DESC") private List<AppComment> comments;
    @Basic(optional = true) @Column(name = "views") private int views;

    public Application() {
        this.views = 0;
    }

    public Application(String appName, String description, User owner, UserGroup group, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) {
        this.name = appName;
        this.description = description;
        this.owner = owner;
        this.group = group;
        this.groupRead = groupRead;
        this.othersRead = othersRead;
        this.groupDownload = groupDownload;
        this.othersDownload = othersDownload;
        this.groupModify = groupModify;
        this.published = published;
        this.views = 0;
    }


    public Application(String appName, String description, Date created, Date updated, User owner, UserGroup group, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) {
        this.name = appName;
        this.description = description;
        this.owner = owner;
        this.group = group;
        this.groupRead = groupRead;
        this.othersRead = othersRead;
        this.groupDownload = groupDownload;
        this.othersDownload = othersDownload;
        this.groupModify = groupModify;
        this.published = published;
        this.created = created;
        this.updated = updated;
        this.views = 0;
    }

    public Application(Integer id, String name, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published, String description, Date created, Date updated, User owner, UserGroup group, Collection<Implementation> implementations, Map<String, AppAttribute> attributes, List<AppComment> comments) {
        this.id = id;
        this.name = name;
        this.groupRead = groupRead;
        this.othersRead = othersRead;
        this.groupDownload = groupDownload;
        this.othersDownload = othersDownload;
        this.groupModify = groupModify;
        this.published = published;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.owner = owner;
        this.group = group;
        this.implementations = implementations;
        this.attributes = attributes;
        this.comments = comments;
        this.views = 0;
    }

    public void incViews(){
        views++;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
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



    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AppComment> getComments() {
        return comments;
    }

    public void addComment(AppComment comment){
        comments.add(comment);
    }

    public void removeComment(AppComment comment){
        comments.remove(comment);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public Boolean getGroupDownload() {
        return groupDownload;
    }

    public void setGroupDownload(Boolean groupDownload) {
        this.groupDownload = groupDownload;
    }

    public Boolean getGroupModify() {
        return groupModify;
    }

    public void setGroupModify(Boolean groupModify) {
        this.groupModify = groupModify;
    }

    public Boolean getGroupRead() {
        return groupRead;
    }

    public void setGroupRead(Boolean groupRead) {
        this.groupRead = groupRead;
    }

    public Collection<Implementation> getImplementations() {
        return implementations;
    }

    public Map<String, AppAttribute> getAttributes(){
        return attributes;
    }

    public void addAttribute(AppAttribute attr) {
        if(attributes == null){
            attributes = new HashMap<String, AppAttribute>();
        }
        attributes.put(attr.getName(), attr);
    }

    public Boolean getOthersDownload() {
        return othersDownload;
    }

    public void setOthersDownload(Boolean othersDownload) {
        this.othersDownload = othersDownload;
    }

    public Boolean getOthersRead() {
        return othersRead;
    }

    public void setOthersRead(Boolean othersRead) {
        this.othersRead = othersRead;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Application)) {
            return false;
        }
        Application other = (Application) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.Application[id=" + id + "]";
    }

}
