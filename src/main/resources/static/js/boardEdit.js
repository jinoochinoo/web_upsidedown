const boardId = document.getElementById('boardId').value;

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
                let savedFileName = target.attr('src').split('/').pop();
                    
                console.log('fileName > ', savedFileName);

                deleteFile(savedFileName);

                // 파일명 삭제
                removeImageFromList(savedFileName);
            }
        }
    });

    const editBtn = document.getElementById('editBtn');
    editBtn.addEventListener('click', handleSubmit);

    // 페이지 로딩 시 게시글 상세 정보 가져오기
    getBoardDetail(boardId);
});

// 게시글 상세 정보를 가져오는 함수
function getBoardDetail(boardId) {
    // API 호출
    fetch('/getBoard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            boardId: boardId
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('게시글을 찾을 수 없습니다.');
        }
        return response.json();
    })
    .then(data => {

        console.log('data > ', data);

        // 데이터 성공적으로 받아오면 화면 세팅
        setBoardDetail(data);
    })
    .catch(error => {
        console.error('Error fetching board detail:', error);
        alert('게시글 정보를 불러오는 데 실패했습니다.');
    });
}

// 게시글 상세 정보를 화면에 세팅하는 함수
function setBoardDetail(board) {
    // 제목 설정
    document.getElementById('title').value = board.title;

    // 내용 설정 (summernote로 초기화)
    $('#summernote').summernote('code', board.content);

    // 첨부 이미지 목록 세팅
    const imageList = document.getElementById('imageList');
    imageList.innerHTML = ''; // 이전 항목들 초기화

    if (board.attachFiles && board.attachFiles.length > 0) {
        board.attachFiles.forEach(file => {
            const listItem = document.createElement('li');
            listItem.id = file.savedFileName;
            listItem.classList.add('list-group-item');
            const fileName = document.createElement('span');
            fileName.innerText = file.originalFileName; // 이미지 파일명
            listItem.appendChild(fileName);
            imageList.appendChild(listItem);
        });
    }
}

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
    document.getElementById(`${savedFileName}`).remove();
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
        id: boardId,
        title: title,
        content: content,
        files: files  // files 배열에 각 파일의 정보 객체들 추가
    };
 
    // JSON 형식으로 서버로 전송
    fetch('/updateBoard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(boardData)
    })
    .then(response => response.json())
    .then(data => {
        console.log('게시글 수정 성공:', data);
        alert("게시물이 수정되었습니다.");
        window.location.href = '/boardList';
    })
    .catch(error => {
        console.error('게시글 수정 실패:', error);
        alert("게시물 수정에 실패했습니다.");
    });
}
