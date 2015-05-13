.386

TITLE	Бондарчук М.Ю., КВ-21

.MODEL	FLAT, C

.DATA
RZ		DB		1

.CODE

; Повертає на першу позицію RZ біт байту AL на позиції AH
; Зберігає значення всіх регістрів
GET_BIT	PROC
		MOV	RZ, 0
		PUSH	AX

		; Якщо треба взяти із нульової позиції
		CMP AH, 0
		JNE	GO1
		; Отримуємо цей біт
		AND	AL, 00000001B
		OR	RZ, AL
		JMP	GOEND
	
		; Якщо треба взяти із нульової позиції
	GO1:
		CMP AH, 1
		JNE	GO2
		; Отримуємо цей біт
		AND	AL, 00000010B
		OR	RZ, AL
		; Виконуємо зсув в RZ (щоб отриманий біт був у молодшому розряді)
		SHR	RZ, 1
		JMP	GOEND

		; І так далі...
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
; Повертає на першу позицію RZ біт з номером AX із M1
; Зберігає значення всіх регістрів
GET_BIT_FROM_M1	PROC
		PUSH	EBP
		MOV		EBP, ESP
		
		; Необхідні нам дані із стека
		M1C		EQU		[EBP + 8 + 12]
		LEN1	EQU		[EBP + 16 + 12]
		
		PUSHA
		MOV		ESI, M1C	; Адреса масиву

		; Знаходимо байт і розряд в ньому
		MOV		CL, 8
		DIV		CL
		CMP		AL, 0		; Ціла частина
		JE		NO
		CMP		AH, 0		; Остача
		JE		NO
	NO:
		
		PUSH	ESI
		MOV		ESI, M1C
		MOV		EBX, 0
		MOV		BL, AL		; В BL - номер байта
		ADD		ESI, EBX

		MOV		AL, BYTE PTR [ESI]
		POP		ESI

		; Отримуємо біт з номером AH в RZ
		CALL	GET_BIT
		
		POPA
		POP		EBP
		RET
GET_BIT_FROM_M1	ENDP

; + 12
; Вставляє на позицію з номером AX у M2 молодший біт RZ
; Зберігає значення всіх регістрів
SET_BIT_TO_M1	PROC
		PUSH	EBP
		MOV		EBP, ESP
		
		; Необхідні нам дані із стека
		M2C		EQU		[EBP + 12 + 12]
		LEN1	EQU		[EBP + 16 + 12]
		PUSHA

		MOV		ESI, M2C

		; Знаходимо байт і розряд в ньому
		MOV		CL, 8
		DIV		CL
		CMP		AH, 0		; Остача
		JE		NO
		CMP		AL, 0		; Ціла частина
		JE		NO
	NO:
		
		MOV		ESI, M2C
		MOV		EBX, 0
		MOV		BL, AL		; В BL - номер байта
		ADD		ESI, EBX

		MOV		CL, AH		; На AL не можна зсувати
		SHL		RZ, CL		; Тепер в RZ біт, що треба вставити
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

		; Параметри процедури
		M1		EQU		[EBP + 8]
		M2		EQU		[EBP + 12]
		LEN		EQU		[EBP + 16]
		IBEG	EQU		[EBP + 20]
		IEND	EQU		[EBP + 24]

		; Обнуляємо M2
		MOV		ECX, DWORD PTR LEN
		MOV		ESI, DWORD PTR M2
		ADD		ESI, LEN
	NULLSTART:
		DEC		ECX
		DEC		ESI
		MOV		BYTE PTR [ESI], 0
		CMP		ECX, 0
		JG		NULLSTART

		; Копіюємо біти відповідно до завдання
		MOV		CX, WORD PTR IBEG
		MOV		DX,	WORD PTR IEND
	MAINLOOP:
		CMP		CX, DX
		JG		MAINLOOPEND

		; Отримуємо поточний біт (він в RZ)
		MOV		AX, CX
		CALL	GET_BIT_FROM_M1

		; Вставляємо його на задану позицію
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