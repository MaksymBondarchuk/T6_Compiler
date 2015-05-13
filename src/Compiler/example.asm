.386

TITLE	��������� �.�., ��-21

.MODEL	FLAT, C

.DATA
RZ		DB		1

.CODE

; ������� �� ����� ������� RZ �� ����� AL �� ������� AH
; ������ �������� ��� �������
GET_BIT	PROC
		MOV	RZ, 0
		PUSH	AX

		; ���� ����� ����� �� ������� �������
		CMP AH, 0
		JNE	GO1
		; �������� ��� ��
		AND	AL, 00000001B
		OR	RZ, AL
		JMP	GOEND
	
		; ���� ����� ����� �� ������� �������
	GO1:
		CMP AH, 1
		JNE	GO2
		; �������� ��� ��
		AND	AL, 00000010B
		OR	RZ, AL
		; �������� ���� � RZ (��� ��������� �� ��� � ��������� ������)
		SHR	RZ, 1
		JMP	GOEND

		; � ��� ���...
	GO2:
		CMP AH, 2
		JNE	GO3
		AND	AL, 00000100B
		OR	RZ, AL
		SHR	RZ, 2
		JMP	GOEND

	GO3:
		CMP AH, 3
		JNE	GO4
		AND	AL, 00001000B
		OR	RZ, AL
		SHR	RZ, 3
		JMP	GOEND

	GO4:
		CMP AH, 4
		JNE	GO5
		AND	AL, 00010000B
		OR	RZ, AL
		SHR	RZ, 4
		JMP	GOEND

	GO5:
		CMP AH, 5
		JNE	GO6
		AND	AL, 00100000B
		OR	RZ, AL
		SHR	RZ, 5
		JMP	GOEND

	GO6:
		CMP AH, 6
		JNE	GO7
		AND	AL, 01000000B
		OR	RZ, AL
		SHR	RZ, 6
		JMP	GOEND
	
	GO7:
		AND	AL, 10000000B
		OR	RZ, AL
		SHR	RZ, 7

	GOEND:
		POP		AX
		RET
GET_BIT	ENDP

; + 12
; ������� �� ����� ������� RZ �� � ������� AX �� M1
; ������ �������� ��� �������
GET_BIT_FROM_M1	PROC
		PUSH	EBP
		MOV		EBP, ESP
		
		; �������� ��� ��� �� �����
		M1C		EQU		[EBP + 8 + 12]
		LEN1	EQU		[EBP + 16 + 12]
		
		PUSHA
		MOV		ESI, M1C	; ������ ������

		; ��������� ���� � ������ � �����
		MOV		CL, 8
		DIV		CL
		CMP		AL, 0		; ֳ�� �������
		JE		NO
		CMP		AH, 0		; ������
		JE		NO
	NO:
		
		PUSH	ESI
		MOV		ESI, M1C
		MOV		EBX, 0
		MOV		BL, AL		; � BL - ����� �����
		ADD		ESI, EBX

		MOV		AL, BYTE PTR [ESI]
		POP		ESI

		; �������� �� � ������� AH � RZ
		CALL	GET_BIT
		
		POPA
		POP		EBP
		RET
GET_BIT_FROM_M1	ENDP

; + 12
; �������� �� ������� � ������� AX � M2 �������� �� RZ
; ������ �������� ��� �������
SET_BIT_TO_M1	PROC
		PUSH	EBP
		MOV		EBP, ESP
		
		; �������� ��� ��� �� �����
		M2C		EQU		[EBP + 12 + 12]
		LEN1	EQU		[EBP + 16 + 12]
		PUSHA

		MOV		ESI, M2C

		; ��������� ���� � ������ � �����
		MOV		CL, 8
		DIV		CL
		CMP		AH, 0		; ������
		JE		NO
		CMP		AL, 0		; ֳ�� �������
		JE		NO
	NO:
		
		MOV		ESI, M2C
		MOV		EBX, 0
		MOV		BL, AL		; � BL - ����� �����
		ADD		ESI, EBX

		MOV		CL, AH		; �� AL �� ����� �������
		SHL		RZ, CL		; ����� � RZ ��, �� ����� ��������
		MOV		AH, RZ
		OR		BYTE PTR [ESI], AH

		POPA
		POP		EBP
		RET
SET_BIT_TO_M1	ENDP

; void Extract(byte* M1, byte* M2, short len, short ibeg, short iend);
Extract	PROC
		PUSH	EBP
		MOV		EBP, ESP
		PUSH	ESI

		; ��������� ���������
		M1		EQU		[EBP + 8]
		M2		EQU		[EBP + 12]
		LEN		EQU		[EBP + 16]
		IBEG	EQU		[EBP + 20]
		IEND	EQU		[EBP + 24]

		; ��������� M2
		MOV		ECX, DWORD PTR LEN
		MOV		ESI, DWORD PTR M2
		ADD		ESI, LEN
	NULLSTART:
		DEC		ECX
		DEC		ESI
		MOV		BYTE PTR [ESI], 0
		CMP		ECX, 0
		JG		NULLSTART

		; ������� ��� �������� �� ��������
		MOV		CX, WORD PTR IBEG
		MOV		DX,	WORD PTR IEND
	MAINLOOP:
		CMP		CX, DX
		JG		MAINLOOPEND

		; �������� �������� �� (�� � RZ)
		MOV		AX, CX
		CALL	GET_BIT_FROM_M1

		; ���������� ���� �� ������ �������
		SUB		AX, WORD PTR IBEG
		CALL	SET_BIT_TO_M1

		INC		CX
		JMP		MAINLOOP
	MAINLOOPEND:

		POP		ESI
		POP		EBP
		RET
Extract	ENDP

END