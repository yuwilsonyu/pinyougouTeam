package com.pinyougou.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Seller 实体类
 * @date 2018-11-03 14:52:44
 * @version 1.0
 */
@Table(name = "tb_seller")
public class Seller implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String sellerId;

    @Column(name = "name")
    private String name;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "status")
    private String status;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "linkman_name")
    private String linkmanName;

    @Column(name = "linkman_qq")
    private String linkmanQq;

    @Column(name = "linkman_mobile")
    private String linkmanMobile;

    @Column(name = "linkman_email")
    private String linkmanEmail;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "org_number")
    private String orgNumber;

    @Column(name = "address")
    private Long address;

    @Column(name = "logo_pic")
    private String logoPic;

    @Column(name = "brief")
    private String brief;

    @Column(name = "create_time")
    private java.util.Date createTime;

    @Column(name = "legal_person")
    private String legalPerson;

    @Column(name = "legal_person_card_id")
    private String legalPersonCardId;

    @Column(name = "bank_user")
    private String bankUser;

    @Column(name = "bank_name")
    private String bankName;


    /** setter and getter method */
    public void setSellerId(String sellerId){
        this.sellerId = sellerId;
    }
    public String getSellerId(){
        return this.sellerId;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    public String getNickName(){
        return this.nickName;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    public String getTelephone(){
        return this.telephone;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setAddressDetail(String addressDetail){
        this.addressDetail = addressDetail;
    }
    public String getAddressDetail(){
        return this.addressDetail;
    }
    public void setLinkmanName(String linkmanName){
        this.linkmanName = linkmanName;
    }
    public String getLinkmanName(){
        return this.linkmanName;
    }
    public void setLinkmanQq(String linkmanQq){
        this.linkmanQq = linkmanQq;
    }
    public String getLinkmanQq(){
        return this.linkmanQq;
    }
    public void setLinkmanMobile(String linkmanMobile){
        this.linkmanMobile = linkmanMobile;
    }
    public String getLinkmanMobile(){
        return this.linkmanMobile;
    }
    public void setLinkmanEmail(String linkmanEmail){
        this.linkmanEmail = linkmanEmail;
    }
    public String getLinkmanEmail(){
        return this.linkmanEmail;
    }
    public void setLicenseNumber(String licenseNumber){
        this.licenseNumber = licenseNumber;
    }
    public String getLicenseNumber(){
        return this.licenseNumber;
    }
    public void setTaxNumber(String taxNumber){
        this.taxNumber = taxNumber;
    }
    public String getTaxNumber(){
        return this.taxNumber;
    }
    public void setOrgNumber(String orgNumber){
        this.orgNumber = orgNumber;
    }
    public String getOrgNumber(){
        return this.orgNumber;
    }
    public void setAddress(Long address){
        this.address = address;
    }
    public Long getAddress(){
        return this.address;
    }
    public void setLogoPic(String logoPic){
        this.logoPic = logoPic;
    }
    public String getLogoPic(){
        return this.logoPic;
    }
    public void setBrief(String brief){
        this.brief = brief;
    }
    public String getBrief(){
        return this.brief;
    }
    public void setCreateTime(java.util.Date createTime){
        this.createTime = createTime;
    }
    public java.util.Date getCreateTime(){
        return this.createTime;
    }
    public void setLegalPerson(String legalPerson){
        this.legalPerson = legalPerson;
    }
    public String getLegalPerson(){
        return this.legalPerson;
    }
    public void setLegalPersonCardId(String legalPersonCardId){
        this.legalPersonCardId = legalPersonCardId;
    }
    public String getLegalPersonCardId(){
        return this.legalPersonCardId;
    }
    public void setBankUser(String bankUser){
        this.bankUser = bankUser;
    }
    public String getBankUser(){
        return this.bankUser;
    }
    public void setBankName(String bankName){
        this.bankName = bankName;
    }
    public String getBankName(){
        return this.bankName;
    }

}