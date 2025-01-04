document.addEventListener('DOMContentLoaded', function() {

    // URL에서 게시글 ID를 추출 (예: /board/detail/{id})
    const boardId = document.getElementById('boardId').value;

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
        document.querySelector('h3').innerText = board.title;

        // 내용 설정 (HTML로 출력)
        document.getElementById('content').innerHTML = board.content;

        // 첨부 이미지 목록 세팅
        const imageList = document.getElementById('imageList');
        imageList.innerHTML = ''; // 이전 항목들 초기화

        if (board.attachFiles && board.attachFiles.length > 0) {
            board.attachFiles.forEach(file => {
                const listItem = document.createElement('li');
                listItem.classList.add('list-group-item');
                const fileName = document.createElement('span');
                fileName.innerText = file.originalFileName; // 이미지 파일명
                listItem.appendChild(fileName);
                imageList.appendChild(listItem);
            });
        } else {
            const noFilesMessage = document.createElement('li');
            noFilesMessage.classList.add('list-group-item');
            noFilesMessage.innerText = '첨부된 이미지가 없습니다.';
            imageList.appendChild(noFilesMessage);
        }

        // 삭제 버튼의 데이터-id 속성 설정 (삭제 기능에 사용)
        const deleteBtn = document.getElementById('deleteBtn');
        
        // 삭제 버튼 클릭 시 동작
        deleteBtn.addEventListener('click', function() {
            if (confirm('정말로 삭제하시겠습니까?')) {
                deleteBoard(boardId);
            }
        });
    }

    // 게시글 삭제 요청 함수
    function deleteBoard(boardId) {

        fetch('/deleteBoard', {
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
                throw new Error('게시글 삭제에 실패했습니다.');
            }
            alert('게시글이 삭제되었습니다.');
            window.location.href = '/boardList'; // 게시글 목록 페이지로 이동
        })
        .catch(error => {
            console.error('Error deleting board:', error);
            alert('게시글 삭제에 실패했습니다.');
        });
    }

    // 페이지 로딩 시 게시글 상세 정보 가져오기
    getBoardDetail(boardId);
});