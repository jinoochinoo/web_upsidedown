document.addEventListener('DOMContentLoaded', function () {

    let selectedRowData = null; // 선택된 데이터 저장 변수

    // 사용자 목록을 불러오는 함수
    function fetchUserList() {
        fetch('/getUserList', {
            method: 'POST', // POST 방식으로 요청
            headers: {
                'Content-Type': 'application/json', // JSON 형식으로 요청
            },
            body: JSON.stringify({}) // 빈 객체를 요청 body에 담아서 보냄
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('사용자 목록을 불러오는 데 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            console.log('data >', data);
            renderUserList(data);
        })
        .catch(error => {
            console.error('에러 발생:', error);
        });
    }

    // 사용자 목록을 화면에 출력하는 함수
    function renderUserList(users) {
        // 사용자 목록을 테이블에 동적으로 추가
        const tableBody = document.querySelector('#userTable tbody');
        tableBody.innerHTML = '';  // 기존 내용 초기화

        // DataTables 초기화
        let userTable = new DataTable('#userTable', {
            data: users,   // 사용자 데이터
            columns: [
                { data: 'id' },
                { data: 'email' },
                { data: 'name' },
                { data: 'phoneNumber' },
                { data: 'company' },
                { data: 'role' }
            ],
            columnDefs: [
                {
                    targets: [0, 1, 2, 3, 4, 5], // 이름, 전화번호, 회사, 권한 컬럼 인덱스
                    className: 'text-center' // 가운데 정렬
                }
            ],
            paging: true,
            pageLength: 10,
            searching: false,  // 검색 기능을 비활성화
            info: false,       // 페이지 현황 표시를 비활성화
            dom: '<"top"i>rt<"bottom"p><"clear">', // 페이지 현황을 제거하고, 페이징만 표시
            initComplete: function () {
                // 페이지네이션을 오른쪽으로 배치하기 위해 nav 태그에 스타일 적용
                $('nav[aria-label="pagination"]').css('float', 'right');
            }
        });

        // 행 클릭 시 선택 상태 토글
        $('#userTable tbody').on('click', 'tr', function () {
            // 이미 선택된 행이 있을 경우 선택 해제
            if ($(this).hasClass('selected')) {
                $(this).removeClass('selected');
                selectedRowData = null; // 선택된 데이터 초기화
            } else {
                // 다른 행을 선택하면 선택 해제 후 새로 선택
                $('#userTable tbody tr.selected').removeClass('selected');
                $(this).addClass('selected');
                // 선택된 행의 데이터 추출
                selectedRowData = userTable.row(this).data();
            }
        });

    }

    // 수정 버튼 클릭 시 선택된 데이터가 있을 경우 정보 수정 팝업
    document.getElementById('updateBtn').addEventListener('click', function () {
        if (selectedRowData) {
            console.log('선택된 데이터:', selectedRowData);

            // 수정 팝업에 선택된 데이터를 표시
            document.getElementById('userId').value = selectedRowData.id;
            document.getElementById('userEmail').value = selectedRowData.email;
            document.getElementById('userName').value = selectedRowData.name;
            document.getElementById('userPhone').value = selectedRowData.phoneNumber;
            document.getElementById('userCompany').value = selectedRowData.company;
            document.getElementById('userRole').value = selectedRowData.role;

            // EMAIL 필드를 수정 불가능하게 설정 (readonly)
            document.getElementById('userEmail').readOnly = true;

            // 모달 띄우기 (Bootstrap 모달)
            $('#userEditModal').modal('show');

        } else {
            alert('수정할 데이터를 선택하세요.');
        }
    });

    // 모달에서 저장 버튼을 클릭했을 때
    document.getElementById('saveChangesBtn').addEventListener('click', function() {
        const updatedData = {
            id: document.getElementById('userId').value,
            email: document.getElementById('userEmail').value,
            name: document.getElementById('userName').value,
            phoneNumber: document.getElementById('userPhone').value,
            company: document.getElementById('userCompany').value,
            role: document.getElementById('userRole').value
        };

        // 데이터 저장 및 서버에 전송
        console.log('수정된 데이터:', updatedData);

        // 예시: 서버로 수정된 데이터 전송 (API 호출 부분)
        fetch('/updateUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedData)
        })
        .then(response => response.json())
        .then(data => {
            alert('사용자 정보가 성공적으로 수정되었습니다.');
            $('#userEditModal').modal('hide'); // 수정 후 모달 닫기
            window.location.href = '/userList';
        })
        .catch(error => {
            console.error('수정 실패:', error);
            alert('사용자 정보 수정에 실패했습니다.');
        });
    });
    
    // 페이지가 로드되면 사용자 목록을 불러옴
    fetchUserList();
});