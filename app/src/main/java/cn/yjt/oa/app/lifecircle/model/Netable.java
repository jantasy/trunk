package cn.yjt.oa.app.lifecircle.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Netable implements Serializable {
    private int FLAG;
    private ArrayList<Netable> Merchants;
    private int adId;
    private String adImgUrl;
    private String adPicUrl;
    private String address;
    private ArrayList<Netable> ads;
    private String alreadyPurchase;
    private int amount;
    private ArrayList<Netable> areas;
    private String attr;
    private ArrayList<Netable> attributes;
    private String author;
    private int business;
    private int bussiness;
    private String[] cities;
    private String code;
    private int collectStatus;
    private String commentCount;
    private ArrayList<Netable> comments;
    private String content;
    private String cost;
    private int count;
    private int couponCount;
    private int couponId;
    private ArrayList<Netable> coupons;
    private String createTime;
    private boolean defaultOption;
    private String description;
    private String discount;
    private String discountDetail;
    private String discountImage;
    private String discountTime;
    private String distance;
    private ArrayList<Netable> distanceOptions;
    private String endTime;
    private String enterTime;
    private String firstType;
    private ArrayList<Netable> firstTypes;
    private String fullName;
    private String generateTime;
    private int guessLike;
    private String iconURL;
    private String iconUrl;
    private int id;
    private String image;
    private String imageUrl;
    private ArrayList<Netable> images;
    private String img;
    private String imgUrl;
    private String ip;
    private int isLocal;
    private int kind;
    private double la;
    private double lo;
    private String locaion;
    private ArrayList<Netable> locals;
    private int maxPurchase;
    private String merchandiseName;
    private ArrayList<Netable> merchandises;
    private String merchantId;
    private String merchantName;
    private ArrayList<Netable> merchants;
    private float money;
    private String name;
    private ArrayList<Netable> notifies;
    private int notifyCount;
    private String offlineDate;
    private String onlineDate;
    private String openTime;
    private ArrayList<Netable> options;
    private Netable order;
    private String orderInfo;
    private ArrayList<Netable> orders;
    private ArrayList<Netable> otherMerchants;
    private String posTime;
    private String postTime;
    private float price;
    private String promotionAbstract;
    private int promotionArea;
    private String promotionImage;
    private String promotionTitle;
    private ArrayList<Netable> provinces;
    private int qnum;
    private String reason;
    private int result;
    private String rule;
    private float score;
    private ArrayList<Netable> secondOrders;
    private String secondType;
    private ArrayList<Netable> secondTypes;
    private String seqNo;
    private String shortName;
    private String startTime;
    private int status;
    private String tel;
    private String tip;
    private String title;
    private int tnum;
    private float totalPrice;
    private String travel;
    private float unitPrice;
    private String url;
    private String useTimeLimit;
    private int vip;
    private ArrayList<String> words;
    private double x;
    private double y;
    private ArrayList<Netable> zones;

    public Netable() {
        this.isLocal = -1;
    }

    public int getAdId() {
        return this.adId;
    }

    public String getAdImgUrl() {
        return this.adImgUrl;
    }

    public String getAdPicUrl() {
        return this.adPicUrl;
    }

    public String getAddress() {
        return this.address;
    }

    public ArrayList<Netable> getAds() {
        return this.ads;
    }

    public String getAlreadyPurchase() {
        return this.alreadyPurchase;
    }

    public int getAmount() {
        return this.amount;
    }

    public ArrayList<Netable> getAreas() {
        return this.areas;
    }

    public String getAttr() {
        return this.attr;
    }

    public ArrayList<Netable> getAttributes() {
        return this.attributes;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getBusiness() {
        return this.business;
    }

    public int getBussiness() {
        return this.bussiness;
    }

    public String[] getCities() {
        return this.cities;
    }

    public String getCode() {
        return this.code;
    }

    public int getCollectStatus() {
        return this.collectStatus;
    }

    public String getCommentCount() {
        return this.commentCount;
    }

    public ArrayList<Netable> getComments() {
        return this.comments;
    }

    public String getContent() {
        return this.content;
    }

    public String getCost() {
        return this.cost;
    }

    public int getCount() {
        return this.count;
    }

    public int getCouponCount() {
        return this.couponCount;
    }

    public int getCouponId() {
        return this.couponId;
    }

    public ArrayList<Netable> getCoupons() {
        return this.coupons;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDiscount() {
        return this.discount;
    }

    public String getDiscountDetail() {
        return this.discountDetail;
    }

    public String getDiscountImage() {
        return this.discountImage;
    }

    public String getDiscountTime() {
        return this.discountTime;
    }

    public String getDistance() {
        return this.distance;
    }

    public ArrayList<Netable> getDistanceOptions() {
        return this.distanceOptions;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getEnterTime() {
        return this.enterTime;
    }

    public int getFLAG() {
        return this.FLAG;
    }

    public String getFirstType() {
        return this.firstType;
    }

    public ArrayList<Netable> getFirstTypes() {
        return this.firstTypes;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getGenerateTime() {
        return this.generateTime;
    }

    public int getGuessLike() {
        return this.guessLike;
    }

    public String getIconURL() {
        return this.iconURL;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public int getId() {
        return this.id;
    }

    public String getImage() {
        return this.image;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ArrayList<Netable> getImages() {
        return this.images;
    }

    public String getImg() {
        return this.img;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public String getIp() {
        return this.ip;
    }

    public int getIsLocal() {
        return this.isLocal;
    }

    public int getKind() {
        return this.kind;
    }

    public double getLa() {
        return this.la;
    }

    public double getLo() {
        return this.lo;
    }

    public String getLocaion() {
        return this.locaion;
    }

    public ArrayList<Netable> getLocals() {
        return this.locals;
    }

    public int getMaxPurchase() {
        return this.maxPurchase;
    }

    public String getMerchandiseName() {
        return this.merchandiseName;
    }

    public ArrayList<Netable> getMerchandises() {
        return this.merchandises;
    }

    public String getMerchantId() {
        return this.merchantId;
    }

    public String getMerchantName() {
        return this.merchantName;
    }

    public ArrayList<Netable> getMerchants() {
        return this.merchants;
    }

    public ArrayList<Netable> getMerchants2() {
        return this.Merchants;
    }

    public float getMoney() {
        return this.money;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Netable> getNotifies() {
        return this.notifies;
    }

    public int getNotifyCount() {
        return this.notifyCount;
    }

    public String getOfflineDate() {
        return this.offlineDate;
    }

    public String getOnlineDate() {
        return this.onlineDate;
    }

    public String getOpenTime() {
        return this.openTime;
    }

    public ArrayList<Netable> getOptions() {
        return this.options;
    }

    public Netable getOrder() {
        return this.order;
    }

    public String getOrderInfo() {
        return this.orderInfo;
    }

    public ArrayList<Netable> getOrders() {
        return this.orders;
    }

    public ArrayList<Netable> getOtherMerchants() {
        return this.otherMerchants;
    }

    public String getPosTime() {
        return this.posTime;
    }

    public String getPostTime() {
        return this.postTime;
    }

    public float getPrice() {
        return this.price;
    }

    public String getPromotionAbstract() {
        return this.promotionAbstract;
    }

    public int getPromotionArea() {
        return this.promotionArea;
    }

    public String getPromotionImage() {
        return this.promotionImage;
    }

    public String getPromotionTitle() {
        return this.promotionTitle;
    }

    public ArrayList<Netable> getProvinces() {
        return this.provinces;
    }

    public int getQnum() {
        return this.qnum;
    }

    public String getReason() {
        return this.reason;
    }

    public int getResult() {
        return this.result;
    }

    public String getRule() {
        return this.rule;
    }

    public float getScore() {
        return this.score;
    }

    public ArrayList<Netable> getSecondOrders() {
        return this.secondOrders;
    }

    public String getSecondType() {
        return this.secondType;
    }

    public ArrayList<Netable> getSecondTypes() {
        return this.secondTypes;
    }

    public String getSeqNo() {
        return this.seqNo;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public int getStatus() {
        return this.status;
    }

    public String getTel() {
        return this.tel;
    }

    public String getTip() {
        return this.tip;
    }

    public String getTitle() {
        return this.title;
    }

    public int getTnum() {
        return this.tnum;
    }

    public float getTotalPrice() {
        return this.totalPrice;
    }

    public String getTravel() {
        return this.travel;
    }

    public float getUnitPrice() {
        return this.unitPrice;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUseTimeLimit() {
        return this.useTimeLimit;
    }

    public int getVip() {
        return this.vip;
    }

    public ArrayList<String> getWords() {
        return this.words;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public ArrayList<Netable> getZones() {
        return this.zones;
    }

    public boolean isDefaultOption() {
        return this.defaultOption;
    }

    public void setAdId(int i) {
        this.adId = i;
    }

    public void setAdImgUrl(String str) {
        this.adImgUrl = str;
    }

    public void setAdPicUrl(String str) {
        this.adPicUrl = str;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public void setAds(ArrayList<Netable> arrayList) {
        this.ads = arrayList;
    }

    public void setAlreadyPurchase(String str) {
        this.alreadyPurchase = str;
    }

    public void setAmount(int i) {
        this.amount = i;
    }

    public void setAreas(ArrayList<Netable> arrayList) {
        this.areas = arrayList;
    }

    public void setAttr(String str) {
        this.attr = str;
    }

    public void setAttributes(ArrayList<Netable> arrayList) {
        this.attributes = arrayList;
    }

    public void setAuthor(String str) {
        this.author = str;
    }

    public void setBusiness(int i) {
        this.business = i;
    }

    public void setBussiness(int i) {
        this.bussiness = i;
    }

    public void setCities(String[] strArr) {
        this.cities = strArr;
    }

    public void setCode(String str) {
        this.code = str;
    }

    public void setCollectStatus(int i) {
        this.collectStatus = i;
    }

    public void setCommentCount(String str) {
        this.commentCount = str;
    }

    public void setComments(ArrayList<Netable> arrayList) {
        this.comments = arrayList;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setCost(String str) {
        this.cost = str;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void setCouponCount(int i) {
        this.couponCount = i;
    }

    public void setCouponId(int i) {
        this.couponId = i;
    }

    public void setCoupons(ArrayList<Netable> arrayList) {
        this.coupons = arrayList;
    }

    public void setCreateTime(String str) {
        this.createTime = str;
    }

    public void setDefaultOption(boolean z) {
        this.defaultOption = z;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public void setDiscount(String str) {
        this.discount = str;
    }

    public void setDiscountDetail(String str) {
        this.discountDetail = str;
    }

    public void setDiscountImage(String str) {
        this.discountImage = str;
    }

    public void setDiscountTime(String str) {
        this.discountTime = str;
    }

    public void setDistance(String str) {
        this.distance = str;
    }

    public void setDistanceOptions(ArrayList<Netable> arrayList) {
        this.distanceOptions = arrayList;
    }

    public void setEndTime(String str) {
        this.endTime = str;
    }

    public void setEnterTime(String str) {
        this.enterTime = str;
    }

    public void setFLAG(int i) {
        this.FLAG = i;
    }

    public void setFirstType(String str) {
        this.firstType = str;
    }

    public void setFirstTypes(ArrayList<Netable> arrayList) {
        this.firstTypes = arrayList;
    }

    public void setFullName(String str) {
        this.fullName = str;
    }

    public void setGenerateTime(String str) {
        this.generateTime = str;
    }

    public void setGuessLike(int i) {
        this.guessLike = i;
    }

    public void setIconURL(String str) {
        this.iconURL = str;
    }

    public void setIconUrl(String str) {
        this.iconUrl = str;
    }

    public void setId(int i) {
        this.id = i;
    }

    public void setImage(String str) {
        this.image = str;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public void setImages(ArrayList<Netable> arrayList) {
        this.images = arrayList;
    }

    public void setImg(String str) {
        this.img = str;
    }

    public void setImgUrl(String str) {
        this.imgUrl = str;
    }

    public void setIp(String str) {
        this.ip = str;
    }

    public void setIsLocal(int i) {
        this.isLocal = i;
    }

    public void setKind(int i) {
        this.kind = i;
    }

    public void setLa(double d) {
        this.la = d;
    }

    public void setLo(double d) {
        this.lo = d;
    }

    public void setLocaion(String str) {
        this.locaion = str;
    }

    public void setLocals(ArrayList<Netable> arrayList) {
        this.locals = arrayList;
    }

    public void setMaxPurchase(int i) {
        this.maxPurchase = i;
    }

    public void setMerchandiseName(String str) {
        this.merchandiseName = str;
    }

    public void setMerchandises(ArrayList<Netable> arrayList) {
        this.merchandises = arrayList;
    }

    public void setMerchantId(String str) {
        this.merchantId = str;
    }

    public void setMerchantName(String str) {
        this.merchantName = str;
    }

    public void setMerchants(ArrayList<Netable> arrayList) {
        this.merchants = arrayList;
    }

    public void setMerchants2(ArrayList<Netable> arrayList) {
        this.Merchants = this.merchants;
    }

    public void setMoney(float f) {
        this.money = f;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setNotifies(ArrayList<Netable> arrayList) {
        this.notifies = arrayList;
    }

    public void setNotifyCount(int i) {
        this.notifyCount = i;
    }

    public void setOfflineDate(String str) {
        this.offlineDate = str;
    }

    public void setOnlineDate(String str) {
        this.onlineDate = str;
    }

    public void setOpenTime(String str) {
        this.openTime = str;
    }

    public void setOptions(ArrayList<Netable> arrayList) {
        this.options = arrayList;
    }

    public void setOrder(Netable netable) {
        this.order = netable;
    }

    public void setOrderInfo(String str) {
        this.orderInfo = str;
    }

    public void setOrders(ArrayList<Netable> arrayList) {
        this.orders = arrayList;
    }

    public void setOtherMerchants(ArrayList<Netable> arrayList) {
        this.otherMerchants = arrayList;
    }

    public void setPosTime(String str) {
        this.posTime = str;
    }

    public void setPostTime(String str) {
        this.postTime = str;
    }

    public void setPrice(float f) {
        this.price = f;
    }

    public void setPromotionAbstract(String str) {
        this.promotionAbstract = str;
    }

    public void setPromotionArea(int i) {
        this.promotionArea = i;
    }

    public void setPromotionImage(String str) {
        this.promotionImage = str;
    }

    public void setPromotionTitle(String str) {
        this.promotionTitle = str;
    }

    public void setProvinces(ArrayList<Netable> arrayList) {
        this.provinces = arrayList;
    }

    public void setQnum(int i) {
        this.qnum = i;
    }

    public void setReason(String str) {
        this.reason = str;
    }

    public void setResult(int i) {
        this.result = i;
    }

    public void setRule(String str) {
        this.rule = str;
    }

    public void setScore(float f) {
        this.score = f;
    }

    public void setSecondOrders(ArrayList<Netable> arrayList) {
        this.secondOrders = arrayList;
    }

    public void setSecondType(String str) {
        this.secondType = str;
    }

    public void setSecondTypes(ArrayList<Netable> arrayList) {
        this.secondTypes = arrayList;
    }

    public void setSeqNo(String str) {
        this.seqNo = str;
    }

    public void setShortName(String str) {
        this.shortName = str;
    }

    public void setStartTime(String str) {
        this.startTime = str;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setTel(String str) {
        this.tel = str;
    }

    public void setTip(String str) {
        this.tip = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setTnum(int i) {
        this.tnum = i;
    }

    public void setTotalPrice(float f) {
        this.totalPrice = f;
    }

    public void setTravel(String str) {
        this.travel = str;
    }

    public void setUnitPrice(float f) {
        this.unitPrice = f;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setUseTimeLimit(String str) {
        this.useTimeLimit = str;
    }

    public void setVip(int i) {
        this.vip = i;
    }

    public void setWords(ArrayList<String> arrayList) {
        this.words = arrayList;
    }

    public void setX(double d) {
        this.x = d;
    }

    public void setY(double d) {
        this.y = d;
    }

    public void setZones(ArrayList<Netable> arrayList) {
        this.zones = arrayList;
    }
}
