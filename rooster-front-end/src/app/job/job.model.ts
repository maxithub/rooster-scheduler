export class Job {
    constructor(
        public name: string, public cronExpr: string
    ) { }
}

export enum HttpAuth {
    Basic, OAuth
}

export class HttpJob extends Job {
    constructor(
        public name: string, public cronExpr: string,
        public url: string, public authType: HttpAuth
    ) {
        super(name, cronExpr);
    }
}

export enum SshAuth {
    Password, Key
}

export class ShellJob extends Job {
    constructor(
        public name: string, public cronExpr: string,
        public host: string, public port: number, public path: string,
        public authType: SshAuth, public username: string, public credential: string
    ) {
        super(name, cronExpr);
    }
}
