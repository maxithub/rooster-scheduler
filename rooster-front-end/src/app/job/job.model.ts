export enum JobType {
    HTTP = 'HTTP',
    Shell = 'Shell'
}

export enum AuthType {
    Basic = 'Basic',
    OAuth = 'OAuth',
    Password = 'Password',
    Key = 'Key'
}

export class Job {
    constructor(
        public name: string,
        public cronExpr: string,
        public jobType: JobType,
        public url: string,
        public host: string,
        public port: number,
        public path: string
    ) {

    }
}
