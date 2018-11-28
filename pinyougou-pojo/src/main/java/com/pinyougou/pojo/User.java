package com.pinyougou.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * User 实体类
 * @date 2018-11-28 09:50:43
 * @version 1.0
 */
public class User implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="username")
	private String username;
	@Column(name="password")
	private String password;
	@Column(name="phone")
	private String phone;
	@Column(name="email")
	private String email;
	@Column(name="created")
	private Date created;
	@Column(name="updated")
	private Date updated;
	@Column(name="source_type")
	private String sourceType;
	@Column(name="nick_name")
	private String nickName;
	@Column(name="name")
	private String name;
	@Column(name="status")
	private String status;
	@Column(name="head_pic")
	private String headPic;
	@Column(name="qq")
	private String qq;
	@Column(name="account_balance")
	private Long accountBalance;
	@Column(name="is_mobile_check")
	private String isMobileCheck;
	@Column(name="is_email_check")
	private String isEmailCheck;
	@Column(name="sex")
	private String sex;
	@Column(name="user_level")
	private Integer userLevel;
	@Column(name="points")
	private Integer points;
	@Column(name="experience_value")
	private Integer experienceValue;
	@Column(name="birthday")
	private Date birthday;
	@Column(name="last_login_time")
	private Date lastLoginTime;
	@Column(name="job")
	private String job;
	@Column(name="province_id")
	private Long provinceId;
	@Column(name="city_id")
	private Long cityId;
	@Column(name="town_id")
	private Long townId;

	/** setter and getter method */
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return this.id;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return this.password;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public String getPhone(){
		return this.phone;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return this.email;
	}
	public void setCreated(java.util.Date created){
		this.created = created;
	}
	public java.util.Date getCreated(){
		return this.created;
	}
	public void setUpdated(java.util.Date updated){
		this.updated = updated;
	}
	public java.util.Date getUpdated(){
		return this.updated;
	}
	public void setSourceType(String sourceType){
		this.sourceType = sourceType;
	}
	public String getSourceType(){
		return this.sourceType;
	}
	public void setNickName(String nickName){
		this.nickName = nickName;
	}
	public String getNickName(){
		return this.nickName;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return this.status;
	}
	public void setHeadPic(String headPic){
		this.headPic = headPic;
	}
	public String getHeadPic(){
		return this.headPic;
	}
	public void setQq(String qq){
		this.qq = qq;
	}
	public String getQq(){
		return this.qq;
	}
	public void setAccountBalance(Long accountBalance){
		this.accountBalance = accountBalance;
	}
	public Long getAccountBalance(){
		return this.accountBalance;
	}
	public void setIsMobileCheck(String isMobileCheck){
		this.isMobileCheck = isMobileCheck;
	}
	public String getIsMobileCheck(){
		return this.isMobileCheck;
	}
	public void setIsEmailCheck(String isEmailCheck){
		this.isEmailCheck = isEmailCheck;
	}
	public String getIsEmailCheck(){
		return this.isEmailCheck;
	}
	public void setSex(String sex){
		this.sex = sex;
	}
	public String getSex(){
		return this.sex;
	}
	public void setUserLevel(Integer userLevel){
		this.userLevel = userLevel;
	}
	public Integer getUserLevel(){
		return this.userLevel;
	}
	public void setPoints(Integer points){
		this.points = points;
	}
	public Integer getPoints(){
		return this.points;
	}
	public void setExperienceValue(Integer experienceValue){
		this.experienceValue = experienceValue;
	}
	public Integer getExperienceValue(){
		return this.experienceValue;
	}
	public void setBirthday(java.util.Date birthday){
		this.birthday = birthday;
	}
	public java.util.Date getBirthday(){
		return this.birthday;
	}
	public void setLastLoginTime(java.util.Date lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}
	public java.util.Date getLastLoginTime(){
		return this.lastLoginTime;
	}
	public void setJob(String job){
		this.job = job;
	}
	public String getJob(){
		return this.job;
	}
	public void setProvinceId(Long provinceId){
		this.provinceId = provinceId;
	}
	public Long getProvinceId(){
		return this.provinceId;
	}
	public void setCityId(Long cityId){
		this.cityId = cityId;
	}
	public Long getCityId(){
		return this.cityId;
	}
	public void setTownId(Long townId){
		this.townId = townId;
	}
	public Long getTownId(){
		return this.townId;
	}

}