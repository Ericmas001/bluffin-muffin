﻿using Com.Ericmas001.Game.Poker.Protocol.Commands.Lobby.Career;
using Com.Ericmas001.Game.Poker.DataTypes;

namespace Com.Ericmas001.Game.Poker.Protocol.Client
{
    public class LobbyTcpClient1Career : LobbyTcpClient1
    {

        private UserInfo m_User;

        public UserInfo User { get { return m_User; } }

        public LobbyTcpClient1Career(string serverAddress, int serverPort)
            : base(serverAddress, serverPort)
        {
        }

        protected override bool GetJoinedSeat(int noPort, string player)
        {
            return base.GetJoinedSeat(noPort, m_User.Username);
        }

        public bool CheckUsernameAvailable(string username)
        {
            Send(new CheckUserExistCommand()
            {
                Username = username,
            });

            return !WaitAndReceive<CheckUserExistResponse>().Exist;
        }

        public bool CheckDisplayNameAvailable(string display)
        {
            Send(new CheckDisplayExistCommand()
            {
                DisplayName = display,
            });

            return !WaitAndReceive<CheckDisplayExistResponse>().Exist;
        }

        public bool CreateUser(string username, string password, string email, string displayname)
        {
            Send(new CreateUserCommand()
            {
                Username = username,
                Password = password,
                Email = email,
                DisplayName = displayname,
            });

            return WaitAndReceive<CreateUserResponse>().Success;
        }

        public bool Authenticate(string username, string password)
        {
            Send(new AuthenticateUserCommand()
            {
                Username = username,
                Password = password,
            });

            return WaitAndReceive<AuthenticateUserResponse>().Success;
        }

        public void RefreshUserInfo(string username)
        {
            Send(new GetUserCommand()
            {
                Username = username,
            });

            var response = WaitAndReceive<GetUserResponse>();
            PlayerName = response.DisplayName;
            m_User = new UserInfo(username, "", response.Email, response.DisplayName, response.Money);
        }
    }
}
