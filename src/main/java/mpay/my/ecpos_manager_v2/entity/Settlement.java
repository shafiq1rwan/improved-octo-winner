package mpay.my.ecpos_manager_v2.entity;

public class Settlement {

	private Long id;
	private String merchantInfo;
	private String bankTID;
	private String bankMID;
	private String batchNo;
	private String transactionDate;
	private String transactionTime;
	private int batchTotal;
	private String nii;

	public Settlement() {
	}

	public Settlement(Long id, String merchantInfo, String bankTID, String bankMID, String batchNo,
			String transactionDate, String transactionTime, int batchTotal, String nii) {
		this.id = id;
		this.merchantInfo = merchantInfo;
		this.bankTID = bankTID;
		this.bankMID = bankMID;
		this.batchNo = batchNo;
		this.transactionDate = transactionDate;
		this.transactionTime = transactionTime;
		this.batchTotal = batchTotal;
		this.nii = nii;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantInfo() {
		return merchantInfo;
	}

	public void setMerchantInfo(String merchantInfo) {
		this.merchantInfo = merchantInfo;
	}

	public String getBankTID() {
		return bankTID;
	}

	public void setBankTID(String bankTID) {
		this.bankTID = bankTID;
	}

	public String getBankMID() {
		return bankMID;
	}

	public void setBankMID(String bankMID) {
		this.bankMID = bankMID;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public int getBatchTotal() {
		return batchTotal;
	}

	public void setBatchTotal(int batchTotal) {
		this.batchTotal = batchTotal;
	}

	public String getNii() {
		return nii;
	}

	public void setNii(String nii) {
		this.nii = nii;
	}

}
