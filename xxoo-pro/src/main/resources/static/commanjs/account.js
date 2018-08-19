//注册
function register(){
	var email = $("#email").val();
	var password = $("#password").val();
	
	if(isEmail(email)==false){
		alert("请输入正确的邮箱格式");
		return
	}else{
		$.ajax({
			url:"/account/zhuce",
			data:{"email":email,"password":password},
			success:function(data){
				if(data.success == false){
					alert(data.information);
				}else{
					alert(data.information+",请您登录")
				}
			}
		});
	}
	
}

//登录
function login(){
	var email = $("#login-email").val();
	var password = $("#login-password").val();
	
	if(email == ''|| password ==''){
		alert("账号或者密码不能为空");
		return;
	}else{
		$.ajax({
			url:"/account/login",
			data:{"email":email,"password":password},
			success:function(data){
				if(data.success == false){
					alert(data.information);
				}else{
					//如果登录成功,显示退出按钮,隐藏登录注册按钮
					alert(data.information);
					window.location.href="/";
				}
			}
		});
	}
}

/**
 * 退出登录
 * @returns
 */
function exit(){
	window.location.href = "/account/exit";
}



$(document).ready(function(){
	$("img").addClass("img-rounded");
	if($("#accountInformation").text()!=='游客'&&$("#accountInformation").text()!==null){
		$("#main-signup-button").fadeOut(1500);
		$("#main-login-button").fadeOut(1500);
		$("#account-information").fadeIn(1500);
		$("#exit").fadeIn(1500);
	}
	
});

//跳转到视频观看的页面
function goVideoPlay(element){
	var id=$(element).attr('data-id');
	if(id !== '' && id !== null){
		window.location="/goVideoPlay?id="+id;
	}
}














