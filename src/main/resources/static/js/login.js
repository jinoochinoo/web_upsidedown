$(document).ready(function() {

    ///////////////////////////// template script start /////////////////////////////
    let signup = $(".links").find("li").find("#signup");
    let signin = $(".links").find("li").find("#signin");
    let reset = $(".links").find("li").find("#reset");
    let first_input = $("form").find(".first-input");
    let hidden_input = $("form").find(".input__block").find(".input_sign_up");
    let signin_btn = $("form").find(".signin__btn");

    //----------- sign up ---------------------
    signup.on("click", function(e) {
        e.preventDefault();
        $(this).parent().parent().siblings("h1").text("SIGN UP");
        $(this).parent().css("opacity", "1");
        $(this).parent().siblings().css("opacity", ".6");
        first_input.removeClass("first-input__block").addClass("signup-input__block");
        hidden_input.css({
            "opacity": "1",
            "display": "block"
        });
        signin_btn.text("Sign up");
        reset.click();
    });


    //----------- sign in ---------------------
    signin.on("click", function(e) {
        e.preventDefault();
        $(this).parent().parent().siblings("h1").text("SIGN IN");
        $(this).parent().css("opacity", "1");
        $(this).parent().siblings().css("opacity", ".6");
        first_input.addClass("first-input__block")
            .removeClass("signup-input__block");
        hidden_input.css({
            "opacity": "0",
            "display": "none"
        });
        signin_btn.text("Sign in");
        reset.click();
    });

    //----------- reset ---------------------
    reset.on("click", function(e) {
        e.preventDefault();
        $(this).parent().parent().siblings("form")
            .find(".input__block").find(".input").val("");
    })

    ///////////////////////////// template script end /////////////////////////////

    document.getElementById('sidebarToggle').style.display = 'none';

    const passwordInput = document.getElementById('password');
    passwordInput.addEventListener('keydown', function(event){
        if(event.key == 'Enter') loginBtn.click();
    });

    const loginBtn = document.getElementById('loginBtn');
    loginBtn.addEventListener('click', function() {

        console.log('loginBtn.text > ', loginBtn.innerText);

        // 회원가입
        if (loginBtn.innerText == 'Sign up') {
            alert('Sign up');

            const passwordValue = document.getElementById('password').value;
            const passwordChkValue = document.getElementById('repeatPassword').value;
            if(passwordValue != passwordChkValue) {
                alert('비밀번호, 비밀번호 확인값이 서로 다릅니다!')
                return;
            }

            const formData = new FormData(document.getElementById('loginForm'));
            const formObject = {};
            formData.forEach((value, key) => {
                    formObject[key] = value;
            });
            
            fetch(`${baseUrl}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formObject)
            })
            .then(response => response.json())
            .then(resultObj => {
                console.log('resultObj > ', resultObj);
                if(resultObj.resultCode == 'SUCCESS'){
                    alert(resultObj.message);
                    location.href=window.location.origin + '/'
                } else{
                    const resultMsg = Object.values(resultObj.data)[0];
                    alert(resultMsg);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        } 
        // 로그인
        else if (loginBtn.innerText == 'Sign in') {
            alert('Sign in')

            const formData = new FormData(document.getElementById('loginForm'));
            const formObject = {};
            formData.forEach((value, key) => {
                if(key == 'email' || key == 'password'){
                    formObject[key] = value;
                }
            });
            
            fetch(`${baseUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formObject)
            })
            .then(response => {
                console.log('response > ', response);
                
                if(response.ok && response.url.includes('main')) {
                    location.href = response.url;
                } else {
                    // 상태 코드가 200이 아닐 경우, JSON 응답 본문을 읽고 에러 메시지 표시
                    response.json().then(data => {
                        console.log('data > ', data);
                        if (data.errMsg) {
                            alert(data.errMsg);  // 서버에서 전달한 에러 메시지
                        } else {
                            alert("아이디 혹은 비밀번호를 확인해주세요!");
                        }
                    }).catch(error => {
                        console.error("Error parsing JSON:", error);
                        alert("로그인 중 오류가 발생했습니다.");
                    });
                }
            });
            /*

            else if(response.status != 200){
                    alert("아이디 혹은 비밀번호를 확인해주세요!")
                }
            .then(resultObj => {
                console.log('resultObj > ', resultObj);
                if(resultObj.resultCode == 'SUCCESS'){
                    alert(resultObj.message);
                    location.href=window.location.origin + '/main'
                } else{
                    const resultMsg = Object.values(resultObj.data)[0];
                    alert(resultMsg);
                }
            })
            .catch((error) => {
                console.log(error);
            });
            */
        }
    });

});

let jwsong = null;