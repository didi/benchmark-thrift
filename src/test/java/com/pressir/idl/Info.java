package com.pressir.idl;

/**
 * Autogenerated by Thrift Compiler (0.11.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2019-09-05")
public class Info implements
        org.apache.thrift.TBase<Info, Info._Fields>,
        java.io.Serializable,
        Cloneable,
        Comparable<Info> {
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC =
            new org.apache.thrift.protocol.TStruct("Info");
    private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC =
            new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short) 2);
    private static final org.apache.thrift.protocol.TField PRICE_FIELD_DESC =
            new org.apache.thrift.protocol.TField("price", org.apache.thrift.protocol.TType.I64, (short) 3);
    private static final org.apache.thrift.protocol.TField SHOP_ID_FIELD_DESC =
            new org.apache.thrift.protocol.TField("shopId", org.apache.thrift.protocol.TType.STRING, (short) 5);
    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY =
            new InfoStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY =
            new InfoTupleSchemeFactory();
    // isset id assignments
    private static final int __PRICE_ISSET_ID = 0;
    private static final _Fields optionals[] = {_Fields.SHOP_ID};

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME,
                new org.apache.thrift.meta_data.FieldMetaData("name",
                        org.apache.thrift.TFieldRequirementType.REQUIRED,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
        tmpMap.put(_Fields.PRICE, new org.apache.thrift.meta_data.FieldMetaData("price",
                org.apache.thrift.TFieldRequirementType.REQUIRED,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
        tmpMap.put(_Fields.SHOP_ID, new org.apache.thrift.meta_data.FieldMetaData("shopId",
                org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Info.class, metaDataMap);
    }

    public java.lang.String name; // required
    public long price; // required
    public java.lang.String shopId; // optional
    private byte __isset_bitfield = 0;

    public Info() {
    }

    public Info(
            java.lang.String name,
            long price) {
        this();
        this.name = name;
        this.price = price;
        setPriceIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public Info(Info other) {
        __isset_bitfield = other.__isset_bitfield;
        if (other.isSetName()) {
            this.name = other.name;
        }
        this.price = other.price;
        if (other.isSetShopId()) {
            this.shopId = other.shopId;
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ?
                STANDARD_SCHEME_FACTORY :
                TUPLE_SCHEME_FACTORY)
                .getScheme();
    }

    public Info deepCopy() {
        return new Info(this);
    }

    @Override
    public void clear() {
        this.name = null;
        setPriceIsSet(false);
        this.price = 0;
        this.shopId = null;
    }

    public java.lang.String getName() {
        return this.name;
    }

    public Info setName(java.lang.String name) {
        this.name = name;
        return this;
    }

    public void unsetName() {
        this.name = null;
    }

    /**
     * Returns true if field name is set (has been assigned a value) and false otherwise
     */
    public boolean isSetName() {
        return this.name != null;
    }

    public void setNameIsSet(boolean value) {
        if (!value) {
            this.name = null;
        }
    }

    public long getPrice() {
        return this.price;
    }

    public Info setPrice(long price) {
        this.price = price;
        setPriceIsSet(true);
        return this;
    }

    public void unsetPrice() {
        __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PRICE_ISSET_ID);
    }

    /**
     * Returns true if field price is set (has been assigned a value) and false otherwise
     */
    public boolean isSetPrice() {
        return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PRICE_ISSET_ID);
    }

    public void setPriceIsSet(boolean value) {
        __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PRICE_ISSET_ID, value);
    }

    public java.lang.String getShopId() {
        return this.shopId;
    }

    public Info setShopId(java.lang.String shopId) {
        this.shopId = shopId;
        return this;
    }

    public void unsetShopId() {
        this.shopId = null;
    }

    /**
     * Returns true if field shopId is set (has been assigned a value) and false otherwise
     */
    public boolean isSetShopId() {
        return this.shopId != null;
    }

    public void setShopIdIsSet(boolean value) {
        if (!value) {
            this.shopId = null;
        }
    }

    public void setFieldValue(_Fields field, java.lang.Object value) {
        switch (field) {
            case NAME:
                if (value == null) {
                    unsetName();
                } else {
                    setName((java.lang.String) value);
                }
                break;

            case PRICE:
                if (value == null) {
                    unsetPrice();
                } else {
                    setPrice((java.lang.Long) value);
                }
                break;

            case SHOP_ID:
                if (value == null) {
                    unsetShopId();
                } else {
                    setShopId((java.lang.String) value);
                }
                break;

        }
    }

    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case NAME:
                return getName();

            case PRICE:
                return getPrice();

            case SHOP_ID:
                return getShopId();

        }
        throw new java.lang.IllegalStateException();
    }

    /**
     * Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
     */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new java.lang.IllegalArgumentException();
        }

        switch (field) {
            case NAME:
                return isSetName();
            case PRICE:
                return isSetPrice();
            case SHOP_ID:
                return isSetShopId();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that == null)
            return false;
        if (that instanceof Info)
            return this.equals((Info) that);
        return false;
    }

    public boolean equals(Info that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_name = true && this.isSetName();
        boolean that_present_name = true && that.isSetName();
        if (this_present_name || that_present_name) {
            if (!(this_present_name && that_present_name))
                return false;
            if (!this.name.equals(that.name))
                return false;
        }

        boolean this_present_price = true;
        boolean that_present_price = true;
        if (this_present_price || that_present_price) {
            if (!(this_present_price && that_present_price))
                return false;
            if (this.price != that.price)
                return false;
        }

        boolean this_present_shopId = true && this.isSetShopId();
        boolean that_present_shopId = true && that.isSetShopId();
        if (this_present_shopId || that_present_shopId) {
            if (!(this_present_shopId && that_present_shopId))
                return false;
            if (!this.shopId.equals(that.shopId))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + ((isSetName()) ? 131071 : 524287);
        if (isSetName())
            hashCode = hashCode * 8191 + name.hashCode();

        hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(price);

        hashCode = hashCode * 8191 + ((isSetShopId()) ? 131071 : 524287);
        if (isSetShopId())
            hashCode = hashCode * 8191 + shopId.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(Info other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.valueOf(isSetName()).compareTo(other.isSetName());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetName()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.valueOf(isSetPrice()).compareTo(other.isSetPrice());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetPrice()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.price, other.price);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.valueOf(isSetShopId()).compareTo(other.isSetShopId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetShopId()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.shopId, other.shopId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
        scheme(iprot).read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
        scheme(oprot).write(oprot, this);
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder sb = new java.lang.StringBuilder("Info(");
        boolean first = true;

        sb.append("name:");
        if (this.name == null) {
            sb.append("null");
        } else {
            sb.append(this.name);
        }
        first = false;
        if (!first) sb.append(", ");
        sb.append("price:");
        sb.append(this.price);
        first = false;
        if (isSetShopId()) {
            if (!first) sb.append(", ");
            sb.append("shopId:");
            if (this.shopId == null) {
                sb.append("null");
            } else {
                sb.append(this.shopId);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
        // check for required fields
        if (name == null) {
            throw new org.apache.thrift.protocol.TProtocolException("Required field 'name' was not present! Struct: "
                    + toString());
        }
        // alas, we cannot check 'price' because it's a primitive and you chose the non-beans generator.
        // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        try {
            write(new org.apache.thrift.protocol.TCompactProtocol(
                    new org.apache.thrift.transport.TIOStreamTransport(out)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
        try {
            // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
            __isset_bitfield = 0;
            read(new org.apache.thrift.protocol.TCompactProtocol(
                    new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    /**
     * The set of fields this struct contains, along with convenience methods for finding and manipulating them.
     */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        NAME((short) 2, "name"),
        PRICE((short) 3, "price"),
        SHOP_ID((short) 5, "shopId");

        private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

        static {
            for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        private final short _thriftId;
        private final java.lang.String _fieldName;

        _Fields(short thriftId, java.lang.String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not found.
         */
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
                case 2: // NAME
                    return NAME;
                case 3: // PRICE
                    return PRICE;
                case 5: // SHOP_ID
                    return SHOP_ID;
                default:
                    return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not found.
         */
        public static _Fields findByName(java.lang.String name) {
            return byName.get(name);
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public java.lang.String getFieldName() {
            return _fieldName;
        }
    }

    private static class InfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        public InfoStandardScheme getScheme() {
            return new InfoStandardScheme();
        }
    }

    private static class InfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<Info> {

        public void read(org.apache.thrift.protocol.TProtocol iprot, Info struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 2: // NAME
                        if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                            struct.name = iprot.readString();
                            struct.setNameIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 3: // PRICE
                        if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                            struct.price = iprot.readI64();
                            struct.setPriceIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 5: // SHOP_ID
                        if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                            struct.shopId = iprot.readString();
                            struct.setShopIdIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    default:
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();

            // check for required fields of primitive type, which can't be checked in the validate method
            if (!struct.isSetPrice()) {
                throw new org.apache.thrift.protocol.TProtocolException(
                        "Required field 'price' was not found in serialized data! Struct: " + toString());
            }
            struct.validate();
        }

        public void write(org.apache.thrift.protocol.TProtocol oprot, Info struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.name != null) {
                oprot.writeFieldBegin(NAME_FIELD_DESC);
                oprot.writeString(struct.name);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(PRICE_FIELD_DESC);
            oprot.writeI64(struct.price);
            oprot.writeFieldEnd();
            if (struct.shopId != null) {
                if (struct.isSetShopId()) {
                    oprot.writeFieldBegin(SHOP_ID_FIELD_DESC);
                    oprot.writeString(struct.shopId);
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class InfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        public InfoTupleScheme getScheme() {
            return new InfoTupleScheme();
        }
    }

    private static class InfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<Info> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, Info struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            oprot.writeString(struct.name);
            oprot.writeI64(struct.price);
            java.util.BitSet optionals = new java.util.BitSet();
            if (struct.isSetShopId()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetShopId()) {
                oprot.writeString(struct.shopId);
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, Info struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            struct.name = iprot.readString();
            struct.setNameIsSet(true);
            struct.price = iprot.readI64();
            struct.setPriceIsSet(true);
            java.util.BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.shopId = iprot.readString();
                struct.setShopIdIsSet(true);
            }
        }
    }
}

