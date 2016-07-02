
public class Status {
	enum Type {
		PRESENT, ABSENT, LEAVE
	}

	private Type type;
	private String reasons;

	public Status(Type type, String reasons) {
		this.type = type;
		this.reasons = reasons;
	}

	public Status(Type type) {
		this(type, "");
	}
	
	public Status() {
		this(Type.PRESENT);
	}

	public Type getType() {
		return type;
	}

	public String getReasons() {
		return reasons;
	}
}
