class folder{
	constructor(parent, name){
		this.parent = parent;
		this.name = name;
	}
}

let root = new folder(null, "/");
root.parent = root;