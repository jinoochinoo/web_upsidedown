$(document).ready(function (){

    // textarea summernote 적용하기
    $("#summernote").summernote({
        codeviewFilter: false,                              // 코드 보기 필터 비활성화
        codeviewIframeFilter: false,                        // 코드 보기 iframe 필터 비활성화

        height: 400,                                        // 에디터 높이
        minHeight: null,                                    // 최소 높이
        maxHeight: null,                                    // 최대 높이
        lang: "ko-KR",                                      // 에디터 한글 설정
        focus : true,                                       // 에디터 포커스 설정
        toolbar: [
            ['fontname', ['fontname']],                     // 글꼴 설정
            ['fontsize', ['fontsize']],                     // 글자 크기
            ['style', ['bold', 'italic', 'underline','strikethrough', 'clear']],  // 글자 스타일 설정
            ['color', ['forecolor','color']],               // 글자색
            ['table', ['table']],                           // 표 생성
            ['insert', ['picture', 'link','video']],        // 이미지, 링크 , 동영상
            ['para', ['ul', 'ol', 'paragraph']],            // 문단 스타일 설정
            ['height', ['height']],                         // 줄간격
            ['view', ['codeview','fullscreen', 'help']]     // 코드보기, 전체화면, 도움말
        ],
        fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋음체','바탕체'], // 추가한 글꼴
        fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72'], // 추가한 폰트사이즈
        callbacks : {
            // 파일 업로드
            onImageUpload : function (files) {
                for(let i=0; i < files.length; i++){
                    // 이미지가 여러개일 경우
                    imageUpload(files[i]);
                }
            },
            // 파일 삭제
            onMediaDelete: function (target){

                console.log('target > ', target);

                if(confirm("이미지를 삭제하시겠습니까?")){
                    let savedFileName = target.attr('src').split('/').pop();
                    
                    console.log('fileName > ', savedFileName);

                    deleteFile(savedFileName);

                    // 파일명 삭제
                    removeImageFromList(savedFileName);
                }
            }
        }
    });

    const writeBtn = document.getElementById('writeBtn');
    writeBtn.addEventListener('click', handleSubmit);
});

function imageUpload(file) {
    let formData = new FormData();
    formData.append('file', file);

    $.ajax({
        url : '/upload/imageUpload',
        type : 'POST',
        data : formData,
        contentType : false,
        processData : false,
        encType : 'multipart/form-data',
        success : function(result) {

            console.log('result > ', result);

            $('#summernote').summernote('insertImage', '/upload/image/' + result.data.savedFileName);

            // 파일명 목록에 추가
            addImageToList(result.data.savedFileName, result.data.originalFileName);
        },
        error(e) {
            console.log('error : ', e);
        }
    });
}

function addImageToList(savedFileName, originalFileName) {
    // 새로운 이미지 항목 생성
    let listItem = `<li class="list-group-item" id=${savedFileName}>
                        <span>${originalFileName}</span>
                    </li>`;
    
    // 이미지 목록에 추가
    $('#imageList').append(listItem);
}

function deleteFile(savedFileName) {
    let formData = new FormData();
    formData.append('fileName', savedFileName);

    $.ajax({
        url : '/upload/imageDelete',
        type : 'POST',
        data : formData,
        contentType : false,
        processData : false,
        encType : 'multipart/form-data'
    });
}

function removeImageFromList(savedFileName) {
    // 해당 파일명과 일치하는 목록 항목 삭제
    document.getElementById(`#${savedFileName}`).remove();
}

function handleSubmit() {

    const title = document.getElementById('title').value;
    const content = $('#summernote').summernote('code');

    // 첨부파일 리스트 수집
    const imageList = document.getElementById("imageList");
    const files = [];

    // 이미지 목록에서 파일명을 수집 (이미지는 파일 객체가 아니라 파일명만 필요)
    for (let i = 0; i < imageList.children.length; i++) {
        const fileItem = imageList.children[i];
        const savedFileName = fileItem.id;  // 이미지 파일의 savedFileName을 사용
        const originalFileName = fileItem.querySelector('span').innerText;
        
        // 파일 정보 객체를 배열에 추가
        files.push({
            savedFileName: savedFileName,
            originalFileName: originalFileName
        });
    }

    // 서버에 전송할 데이터 준비
    const boardData = {
        title: title,
        content: content,
        files: files  // files 배열에 각 파일의 정보 객체들 추가
    };
 
    // JSON 형식으로 서버로 전송
    fetch('/writeBoard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(boardData)
    })
    .then(response => response.json())
    .then(data => {
        console.log('게시글 작성 성공:', data);
        alert("게시물이 등록되었습니다.");
    })
    .catch(error => {
        console.error('게시글 작성 실패:', error);
        alert("게시물 등록에 실패했습니다.");
    });
}
