# reactive-java
лабы делаем
Для начла мы создали базовые классов и генераторов объектов
Создаем систему чатов с пользователями и сообщениями. Вот основные классы:

User – пользователь, который участвует в чате.
Message – сообщение, которое отправляется в чате.
Chat – сам чат, где ведется общение, содержит сообщения и пользователей.
Основные классы:
В классе User будут поля разных типов, включая примитивы, String, дату/время, enum, record и коллекцию.
В классе Message можно указать текст сообщения и автора.
В классе Chat будет коллекция сообщений и пользователей.
Поля для класса User:
int age (примитив),
String name (имя пользователя),
LocalDateTime registrationDate (дата регистрации),
Role role (перечисление),
ContactInfo contactInfo (как record, для хранения информации о пользователе),
Список друзей (List<User>).
Дополнительные классы:
Role – это перечисление, содержащее типы ролей (например, ADMIN, USER).
ContactInfo – это record, содержащее информацию о пользователе (например, телефон и email).