struct Info{
    2: required string name;
    3: required i64 price;
    5: optional string shopId;
}

enum ShopStatus{
    OPEN = 1 ;
    CLOSE = 2 ;
}

struct Shop{
    3: required string shopId;
    5: required ShopStatus status;
}

service Soda{
    ShopStatus getShopStatus(1: string shopId);
    list<Info> getInfos(1: Shop shop);
}
