package gov.hhs.cms.base.common.util.x12mapper;

public enum X12Mapper implements Mapper{
	GenderType {
		public String getX12Mapping(String ffmCode) {
			if (ffmCode == null){
				throw new IllegalArgumentException("Null code!");
			}
			if (ffmCode.equals("1")){
				//Corresponds to Male in FFE system
				return "M";
			}
			if (ffmCode.equals("2")){
				//Corresponds to Female in FFE system
				return "F";
			}
			if (ffmCode.equals("3")){
				//Corresponds to No Preference in FFE system
				return "U";
			}
			throw new IllegalArgumentException("No mapping exists for '" + ffmCode + "'!");
		}
		
	},
	MemberMaritalStatusType{
		public String getX12Mapping(String ffmCode) {
			if (ffmCode == null){
				throw new IllegalArgumentException("Null code!");
			}
			if (ffmCode.equals("1")){
				//Corresponds to Unmarried in FFE system
				return "U";
			}
			if (ffmCode.equals("2")){
				//Corresponds to Married in FFE system
				return "M";
			}
			throw new IllegalArgumentException("No mapping exists for '" + ffmCode + "'!");
		}
		
	},
	InsuranceProductDivisionType{
		public String getX12Mapping(String ffmCode){
			if (ffmCode == null){
				throw new IllegalArgumentException("Null code!");
			}
			if (ffmCode.equals("1")){
				//Corresponds to Healthcare in FFE system
				return "HLT";
			}
			if (ffmCode.equals("2")){
				//Corresponds to Dental in FFE system
				return "DEN";
			}
			if (ffmCode.equals("3")){
				//Corresponds to Vision in FFE system
				return "VIS";
			}
			//Note: original Excel sheet does not have a mapping
			//for 4 (Healthcare and Dental), and so no mapping is placed.
			throw new IllegalArgumentException("No mapping exists for '" + ffmCode + "'!");
		}
	}
}

interface Mapper{
	String getX12Mapping(String ffmCode);
}