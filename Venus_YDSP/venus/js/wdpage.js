if(!PhoneGap.hasResource("wdpage")) {
PhoneGap.addResource("wdpage");
var wdpage = function() {
	
};

//@function navigator.wdpage.loadpage
//@params url
//@return нч
wdpage.prototype.loadpage = function(url){
	console.log("user js call loadpage")
	PhoneGap.exec(null,null,
	'WDPage',
	'loadpage',
	[url]);
};

PhoneGap.addConstructor(function() {
    if (typeof navigator.wdpage === "undefined") {
		console.log("Constructor wdpage Plugin")
        navigator.wdpage = window.wdpage = new wdpage();
    }
});
}