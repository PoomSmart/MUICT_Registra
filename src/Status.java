
public class Status {
	enum Type {
		PRESENT, ABSENT, LEAVE
	}

	private Type type;
	private String reason;
	
	public static Type getType(String type) {
		switch (type) {
		case "present":
			return Type.PRESENT;
		case "absent":
			return Type.ABSENT;
		case "leave":
			return Type.LEAVE;
		}
		return null;
	}

	public Status(Type type, String reason) {
		this.type = type;
		this.reason = reason;
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

	public String getReason() {
		return reason;
	}
	
	public String toString() {
		if (type == null)
			return ""; // It should not happen
		return type.toString();
	}
}
