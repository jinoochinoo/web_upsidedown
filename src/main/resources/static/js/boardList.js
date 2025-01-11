document.addEventListener('DOMContentLoaded', function() {
    // 게시글 목록을 불러오는 함수
    function fetchBoardList() {
        fetch('/getBoardList', {
            method: 'POST', // POST 방식으로 요청
            headers: {
                'Content-Type': 'application/json', // JSON 형식으로 요청
            },
            body: JSON.stringify({}) // 빈 객체를 요청 body에 담아서 보냄
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('게시글 목록을 불러오는 데 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {

            console.log('data > ', data);

            renderBoardList(data);
        })
        .catch(error => {
            console.error('에러 발생:', error);
        });
    }

    // 게시글 목록을 화면에 출력하는 함수
    function renderBoardList(boards) {
        const boardListElement = document.getElementById('board-list');
        boardListElement.innerHTML = ''; // 기존 목록을 지움

        // 각 게시글을 카드로 변환하여 HTML에 추가
        boards.forEach(board => {
            const colDiv = document.createElement('div');
            colDiv.classList.add('col-12', 'col-md-6', 'col-lg-4', 'mb-4');

            const cardDiv = document.createElement('div');
            cardDiv.classList.add('card');

            // 첨부파일이 있다면 첫 번째 파일의 savedFileName을 사용하여 이미지 경로 설정
            let imgSrc = 'https://via.placeholder.com/500x300?text=Image'; // 기본 이미지

            if (board.files && board.files.length > 0) {
                const firstFile = board.files[0];
                imgSrc = '/upload/image/' + firstFile.savedFileName; // 첫 번째 파일의 savedFileName으로 경로 설정
            }

            // 게시글 이미지
            const imgElement = document.createElement('img');
            imgElement.classList.add('card-img-top');
            imgElement.setAttribute('src', imgSrc); // 동적으로 이미지 경로 설정
            imgElement.setAttribute('alt', 'Card image cap');

            // 카드 본문
            const cardBodyDiv = document.createElement('div');
            cardBodyDiv.classList.add('card-body');

            // 게시글 제목
            const cardTitle = document.createElement('h4');
            cardTitle.classList.add('card-title');
            cardTitle.textContent = board.title;

            // 게시글 내용을 짧게 (간단히) 표시
            //const cardText = document.createElement('p');
            //cardText.classList.add('card-text');
            //cardText.textContent = board.content.substring(0, 100) + '...'; // 내용의 일부

            // 카드 클릭 시 GET 방식으로 boardDetail 페이지로 이동하도록 설정
            cardDiv.addEventListener('click', function() {
                // boardId를 파라미터로 해서 boardDetail 페이지로 이동
                window.location.href = `/boardDetail?boardId=${board.id}`;
            });

            // 카드에 요소 추가
            cardBodyDiv.appendChild(cardTitle);
            //cardBodyDiv.appendChild(cardText);
            cardDiv.appendChild(imgElement);
            cardDiv.appendChild(cardBodyDiv);

            // 카드 div를 col div에 추가
            colDiv.appendChild(cardDiv);

            // 최종적으로 board-list에 카드 추가
            boardListElement.appendChild(colDiv);
        });
    }

    // 페이지가 로드되면 게시글 목록을 불러옴
    fetchBoardList();

    const writeBtn = document.getElementById('writeBtn');
    writeBtn.addEventListener('click', function() {
        window.location.href = '/boardWrite';
    });
});