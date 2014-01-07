/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edward
 */
@Entity
@Table(name = "ratings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ratings.findAll", query = "SELECT r FROM Ratings r"),
    @NamedQuery(name = "Ratings.findById", query = "SELECT r FROM Ratings r WHERE r.id = :id"),
    @NamedQuery(name = "Ratings.findByImpId", query = "SELECT r FROM Ratings r WHERE r.versionID = :imp"),
    @NamedQuery(name = "Ratings.findByImpIdAndUserId", query = "SELECT r FROM Ratings r WHERE r.versionID = :imp and r.userID =:id"),
    @NamedQuery(name = "Ratings.findByRate", query = "SELECT r FROM Ratings r WHERE r.rate = :rate")})
public class Ratings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rate")
    private int rate;
    @JoinColumn(name = "userID", referencedColumnName = "login_name")
    @ManyToOne
    private User userID;
    @JoinColumn(name = "versionID", referencedColumnName = "id")
    @ManyToOne
    private Implementation versionID;

    public Ratings() {
    }

    public Ratings(Integer id) {
        this.id = id;
    }

    public Ratings(Integer id, int rate) {
        this.id = id;
        this.rate = rate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }

    public Implementation getVersionID() {
        return versionID;
    }

    public void setVersionID(Implementation versionID) {
        this.versionID = versionID;
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
        if (!(object instanceof Ratings)) {
            return false;
        }
        Ratings other = (Ratings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.Ratings[ id=" + id + " ]";
    }

}
