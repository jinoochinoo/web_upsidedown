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
            onMediaDelete: function ($target){
                if(confirm("이미지를 삭제하시겠습니까?")){
                    let fileName = $target.attr('src').split('/').pop();
                    deleteFile(fileName);
                }
            }
        }
    });
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
        },
        error(e) {
            console.log('error : ', e);
        }
    });
}

function deleteFile(fileName) {
    let formData = new FormData();
    formData.append('file', fileName);

    $.ajax({
        url : '/upload/imageDelete',
        type : 'POST',
        data : formData,
        contentType : false,
        processData : false,
        encType : 'multipart/form-data'
    });
}