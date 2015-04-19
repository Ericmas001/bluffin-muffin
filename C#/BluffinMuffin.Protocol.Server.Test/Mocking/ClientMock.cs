﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using BluffinMuffin.Protocol.Commands;

namespace BluffinMuffin.Protocol.Server.Test.Mocking
{
    public class ClientMock : IBluffinClient
    {
        private ServerMock m_Server;
        public ClientMock(ServerMock server)
        {
            m_Server = server;
        }

        public string PlayerName { get; set; }
        public void SendCommand(AbstractBluffinCommand command)
        {
            m_Server.ServerSendedCommands.Add(new CommandEntry() { Client = this, Command = command });
        }

        public void AddPlayer(RemotePlayer p)
        {
            throw new NotImplementedException();
        }

        public void RemovePlayer(RemotePlayer p)
        {
            throw new NotImplementedException();
        }
    }
}
